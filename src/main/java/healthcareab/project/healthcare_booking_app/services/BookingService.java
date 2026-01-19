package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.*;
import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.exceptions.ConflictException;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.exceptions.UnauthorizedException;
import healthcareab.project.healthcare_booking_app.helpers.email.SESEmailHelper;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.BookingRepository;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingConverter bookingConverter;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final SESEmailHelper sesEmailHelper;

    public BookingService(BookingRepository bookingRepository, BookingConverter bookingConverter, AuthService authService, UserRepository userRepository, SESEmailHelper sesEmailHelper) {
        this.bookingRepository = bookingRepository;
        this.bookingConverter = bookingConverter;
        this.authService = authService;
        this.userRepository = userRepository;
        this.sesEmailHelper = sesEmailHelper;
    }

    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        Booking booking = new Booking();

        //Set patient to authorized patient.
        User patient = authService.getAuthenticated();
        User caregiver = userRepository.findById(request.getCaregiverId()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));


        booking.setPatientId(patient.getId());
        booking.setCaregiverId(request.getCaregiverId());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setStartDateTime(request.getStartDateTime());
        booking.setEndDateTime(request.getEndDateTime());
        booking.setSymptoms(request.getSymptoms());
        booking.setReasonForVisit(request.getReasonForVisit());
        booking.setNotesFromCaregiver(request.getNotesFromPatient());

        Booking createdBooking = bookingRepository.save(booking);
        String message = "Booking confirmed for " + patient.getFirstName() + " " + patient.getLastName() + " at " + booking.getStartDateTime() + " with caregiver " + caregiver.getFirstName() + " " + caregiver.getLastName();
        sesEmailHelper.sendEmail(message, "Confirmation Email", patient.getEmail());

        return bookingConverter.convertToCreateBookingResponse(createdBooking, caregiver);
    }

    public List<GetBookingsResponse> getMyBookings() {
        User user = authService.getAuthenticated();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().contains(Role.CAREGIVER)) {
            List<Booking> bookings = bookingRepository.findByCaregiverIdOrderByStartDateTimeDesc(user.getId());
            List<GetBookingsResponse> responses = new ArrayList<>();
            // For each booking, find the patients name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User patient = userRepository.findById(booking.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                String fullName = patient.getFirstName() + " " + patient.getLastName();
                responses.add(bookingConverter.convertToGetBookingsResponse(booking, fullName));
            }
            return responses;

        } else if (user.getRoles().contains(Role.PATIENT)) {
            List<Booking> bookings = bookingRepository.findByPatientIdOrderByStartDateTimeDesc(user.getId());
            List<GetBookingsResponse> responses = new ArrayList<>();
            // For each booking, find the caregivers name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User caregiver = userRepository.findById(booking.getCaregiverId()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));
                String fullName = caregiver.getFirstName() + " " + caregiver.getLastName();
                responses.add(bookingConverter.convertToGetBookingsResponse(booking, fullName));
            }
            return responses;

        } else {
            throw new AccessDeniedException("You are not authorized to view bookings");
        }
    }

    public List<GetBookingHistoryResponse> getMyBookingHistory() {
        User user = authService.getAuthenticated();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().contains(Role.CAREGIVER)) {
            // Gets only past bookings
            List<Booking> bookings = bookingRepository.findByCaregiverIdAndEndDateTimeBeforeOrderByStartDateTimeDesc(user.getId(), LocalDateTime.now());
            List<GetBookingHistoryResponse> responses = new ArrayList<>();
            // For each booking, find the patients name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User patient = userRepository.findById(booking.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                String fullName = patient.getFirstName() + " " + patient.getLastName();
                responses.add(bookingConverter.convertToGetBookingHistoryResponse(booking, fullName));
            }
            return responses;

        } else if (user.getRoles().contains(Role.PATIENT)) {
            // Gets only past bookings
            List<Booking> bookings = bookingRepository.findByPatientIdAndEndDateTimeBeforeOrderByStartDateTimeDesc(user.getId(), LocalDateTime.now());
            List<GetBookingHistoryResponse> responses = new ArrayList<>();
            // For each booking, find the caregivers name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User caregiver = userRepository.findById(booking.getCaregiverId()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));
                String fullName = caregiver.getFirstName() + " " + caregiver.getLastName();
                responses.add(bookingConverter.convertToGetBookingHistoryResponse(booking, fullName));
            }
            return responses;

        } else {
            throw new AccessDeniedException("You are not authorized to view bookings");
        }
    }

    public Optional<GetNextBookingResponse> getNextBooking() {
        User user = authService.getAuthenticated();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().contains(Role.CAREGIVER)) {
            Booking booking = bookingRepository.findFirstByCaregiverIdAndStartDateTimeAfterOrderByStartDateTimeAsc(user.getId(), LocalDateTime.now());
            if (booking != null) {
                User patient = userRepository.findById(booking.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                String dayOfWeek = booking.getStartDateTime().getDayOfWeek().toString();
                return Optional.of(
                        bookingConverter.convertToGetNextBookingResponse(
                                booking, dayOfWeek,
                                patient.getFirstName() + " " +
                                        patient.getLastName()));
            }
        } else if (user.getRoles().contains(Role.PATIENT)) {
            Booking booking = bookingRepository.findFirstByPatientIdAndStartDateTimeAfterOrderByStartDateTimeAsc(user.getId(), LocalDateTime.now());
            if (booking != null) {
            User caregiver = userRepository.findById(booking.getCaregiverId()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));
            String dayOfWeek = booking.getStartDateTime().getDayOfWeek().toString();
            return Optional.of(
                    bookingConverter.convertToGetNextBookingResponse(
                            booking,
                            dayOfWeek,
                            caregiver.getFirstName() + " " +
                                    caregiver.getLastName()));
            }
        }
        return Optional.empty();
    }

    public PatchBookingResponse cancelBooking(String bookingId) {

        // Get booking by booking id or else throw exception
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Get current logged-in user (patient)
        User patient = authService.getAuthenticated();

        // Check if current user (patient) is same as patient in booking
        if (!booking.getPatientId().equals(patient.getId())) {
            throw new UnauthorizedException("You do not have permission to cancel this booking");
        }

        // Throw exception if current status of booking is already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("This booking has already been cancelled");
        }

        // Check if booking date has already been
        if (booking.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new ConflictException("This booking has already passed");
        }

        // Check if there's a minimum of 24 hours before the appointment
        Duration durationUntilStart = Duration.between(LocalDateTime.now(), booking.getStartDateTime());

        if (durationUntilStart.toHours() < 24) {
            throw new IllegalArgumentException("Booking cannot be cancelled within 24 hours of start time");

        }

        // Get caregiver
        User caregiver = userRepository.findById(booking.getCaregiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));

        // Cancel booking and return DTO
        booking.setStatus(BookingStatus.CANCELLED);
        Booking createdBooking = bookingRepository.save(booking);
        return bookingConverter.convertToPatchBookingResponse(createdBooking, caregiver);

    }
}
