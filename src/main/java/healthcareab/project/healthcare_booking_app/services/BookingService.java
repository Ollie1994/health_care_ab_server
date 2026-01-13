package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingHistoryResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingsResponse;
import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.helpers.email.SESEmailHelper;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.BookingRepository;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        if (user.getRoles().contains(Role.PATIENT)) {
            List<Booking> bookings = bookingRepository.findByPatientId(user.getId());
            List<GetBookingsResponse> responses = new ArrayList<>();
            // For each booking, find the caregivers name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User caregiver = userRepository.findById(booking.getCaregiverId()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));
                String fullName = caregiver.getFirstName() + " " + caregiver.getLastName();
                responses.add(bookingConverter.convertToGetBookingsResponse(booking, fullName));
            }
            return responses;

        } else if (user.getRoles().contains(Role.CAREGIVER)) {
            List<Booking> bookings = bookingRepository.findByCaregiverId(user.getId());
            List<GetBookingsResponse> responses = new ArrayList<>();
            // For each booking, find the patients name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User patient = userRepository.findById(booking.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                String fullName = patient.getFirstName() + " " + patient.getLastName();
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

        if (user.getRoles().contains(Role.PATIENT)) {
            // Gets only past bookings
            List<Booking> bookings = bookingRepository.findByPatientIdAndEndDateTimeBefore(user.getId(), LocalDateTime.now());
            List<GetBookingHistoryResponse> responses = new ArrayList<>();
            // For each booking, find the caregivers name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User caregiver = userRepository.findById(booking.getCaregiverId()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));
                String fullName = caregiver.getFirstName() + " " + caregiver.getLastName();
                responses.add(bookingConverter.convertToGetBookingHistoryResponse(booking, fullName));
            }
            return responses;

        } else if (user.getRoles().contains(Role.CAREGIVER)) {
            // Gets only past bookings
            List<Booking> bookings = bookingRepository.findByCaregiverIdAndEndDateTimeBefore(user.getId(), LocalDateTime.now());
            List<GetBookingHistoryResponse> responses = new ArrayList<>();
            // For each booking, find the patients name, convert to DTO and add to response.
            for (Booking booking : bookings) {
                User patient = userRepository.findById(booking.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                String fullName = patient.getFirstName() + " " + patient.getLastName();
                responses.add(bookingConverter.convertToGetBookingHistoryResponse(booking, fullName));
            }
            return responses;

        } else {
            throw new AccessDeniedException("You are not authorized to view bookings");
        }
    }
}
