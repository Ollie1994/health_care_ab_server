package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.helpers.email.SESEmailHelper;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.BookingRepository;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        sesEmailHelper.sendEmail();

        return bookingConverter.convertToCreateBookingResponse(createdBooking, caregiver);
    }


}
