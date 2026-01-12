package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.BookingRepository;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ResponseEntity<?> getMyBookings() {
        User user = authService.getAuthenticated();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().contains(Role.PATIENT)) {
            List<Booking> bookings = bookingRepository.findByPatient_id(user.getId());
        } else if (user.getRoles().contains(Role.CAREGIVER)) {

        } else {
            throw new AccessDeniedException("You are not authorized to view this booking");
        }
    }

}
