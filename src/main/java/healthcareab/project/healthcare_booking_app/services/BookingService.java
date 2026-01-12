package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingsResponse;
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

import java.awt.print.Book;
import java.util.ArrayList;
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

    public List<GetBookingsResponse> getMyBookings() {
        User user = authService.getAuthenticated();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRoles().contains(Role.PATIENT)) {
            List<Booking> bookings = bookingRepository.findByPatientId(user.getId());
            List<GetBookingsResponse> responses = new ArrayList<>();
            for (Booking booking : bookings) {
                User caregiver = userRepository.findById(booking.getCaregiver_id()).orElseThrow(() -> new ResourceNotFoundException("Caregiver not found"));
                String fullName = caregiver.getFirstName() + " " + caregiver.getLastName();
                responses.add(convertToGetBookingsResponse(booking, fullName));
            }
            return responses;
        } else if (user.getRoles().contains(Role.CAREGIVER)) {
            List<Booking> bookings = bookingRepository.findByCaregiverId(user.getId());
            List<GetBookingsResponse> responses = new ArrayList<>();
            for (Booking booking : bookings) {
                User patient = userRepository.findById(booking.getPatient_id()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                String fullName = patient.getFirstName() + " " + patient.getLastName();
                responses.add(convertToGetBookingsResponse(booking, fullName));
            }
            return responses;
        } else {
            throw new AccessDeniedException("You are not authorized to view this booking");
        }
    }

    private GetBookingsResponse convertToGetBookingsResponse (Booking booking, String fullName) {
        return new GetBookingsResponse(booking.getStart_date_time(), booking.getEnd_date_time(), booking.getStatus(), fullName, booking.getSymptoms(), booking.getId());
    }
}
