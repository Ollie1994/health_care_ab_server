package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.PatchBookingResponse;
import healthcareab.project.healthcare_booking_app.exceptions.ConflictException;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.exceptions.UnauthorizedException;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.BookingRepository;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingConverter bookingConverter;
    private final AuthService authService;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, BookingConverter bookingConverter, AuthService authService, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingConverter = bookingConverter;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        Booking booking = new Booking();

        //Set patient to authorized patient.
        User patient = authService.getAuthenticated();
        User caregiver = userRepository.findById(request.getCaregiver_id()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));



        booking.setPatient_id(patient.getId());
        booking.setCaregiver_id(request.getCaregiver_id());
        booking.setStatus(BookingStatus.PENDING);
        booking.setStart_date_time(request.getStart_date_time());
        booking.setEnd_date_time(request.getEnd_date_time());
        booking.setSymptoms(request.getSymptoms());
        booking.setReason_for_visit(request.getReason_for_visit());
        booking.setNotes_from_patient(request.getNotes_from_patient());

        Booking createdBooking = bookingRepository.save(booking);

        return bookingConverter.convertToCreateBookingResponse(createdBooking, caregiver);
    }

    public PatchBookingResponse cancelBooking(String bookingId) {

        // Get booking by booking id or else throw exception
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Get current logged-in user (patient)
        User patient = authService.getAuthenticated();

        // Check if current user (patient) is same as patient in booking
        if (!booking.getPatient_id().equals(patient.getId())) {
            throw new UnauthorizedException("You do not have permission to cancel this booking");
        }

        // Throw exception if current status of booking is already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("This booking has already been cancelled");
        }

        // Check if booking date has already been
        if (booking.getEnd_date_time().isBefore(LocalDateTime.now())) {
            throw new ConflictException("This booking has already passed");
        }

        // Check if there's a minimum of 24 hours before the appointment
        Duration durationUntilStart = Duration.between(LocalDateTime.now(), booking.getStart_date_time());

        if (durationUntilStart.toHours() < 24) {
            throw new IllegalArgumentException("Booking cannot be cancelled within 24 hours of start time");

        }

        // Get caregiver
        User caregiver = userRepository.findById(booking.getCaregiver_id())
                .orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));

        // Cancel booking and return DTO
        booking.setStatus(BookingStatus.CANCELLED);
        Booking createdBooking = bookingRepository.save(booking);
        return bookingConverter.convertToPatchBookingResponse(createdBooking, caregiver);

    }

}
