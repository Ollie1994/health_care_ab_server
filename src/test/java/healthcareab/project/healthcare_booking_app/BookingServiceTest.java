package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.helpers.email.SESEmailHelper;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.BookingRepository;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import healthcareab.project.healthcare_booking_app.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SESEmailHelper sesEmailHelper;

    @Mock
    private BookingConverter bookingConverter;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User patient;
    private User caregiver;
    private CreateBookingRequest request;
    private Booking savedBooking;
    private CreateBookingResponse expectedResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // --- ARRANGE COMMON DATA ---
        patient = new User("patient", "password", null);
        caregiver = new User("caregiver", "password", null);


        request = new CreateBookingRequest(
                caregiver.getId(),
                LocalDateTime.of(2026, 8, 5, 10, 0),
                LocalDateTime.of(2026, 8, 5, 11, 0),
                List.of("fever", "cough"),
                "Checkup",
                "Feeling tired"
        );

        savedBooking = new Booking(); // what repo would return

        expectedResponse = new CreateBookingResponse(
                "Booking created",
                caregiver.getUsername(),
                request.getStartDateTime(),
                request.getEndDateTime()
        );
    }

    @Test
    void createBooking_shouldReturnBookingResponse() {
        // --- ARRANGE ---
        when(authService.getAuthenticated()).thenReturn(patient);
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingConverter.convertToCreateBookingResponse(savedBooking, caregiver)).thenReturn(expectedResponse);
        doNothing().when(sesEmailHelper).sendEmail(anyString(), anyString(), any());

        // --- ACT ---
        CreateBookingResponse actualResponse = bookingService.createBooking(request);

        // --- ASSERT ---
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        // Verify interactions
        verify(authService).getAuthenticated();
        verify(userRepository).findById(caregiver.getId());
        verify(bookingRepository).save(any(Booking.class));
        verify(bookingConverter).convertToCreateBookingResponse(savedBooking, caregiver);
        verify(sesEmailHelper).sendEmail(anyString(), anyString(), any());
    }
}