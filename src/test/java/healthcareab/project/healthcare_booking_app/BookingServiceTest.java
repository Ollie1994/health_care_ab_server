package healthcareab.project.healthcare_booking_app;

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
import healthcareab.project.healthcare_booking_app.services.AuthService;
import healthcareab.project.healthcare_booking_app.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

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
    private Booking booking;
    private PatchBookingResponse expectedPatchResponse;

    // --- So user id does not throw NullPointerException in cancel booking tests
    private void setUserId(User user, String id) throws Exception {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
    }

    @BeforeEach
    void setup() throws Exception {
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
                request.getStart_date_time(),
                request.getEnd_date_time()
        );

    // -------------------- CANCEL BOOKING TESTS --------------------

        // --- ARRANGE COMMON DATA FOR CANCEL BOOKING ---

        // Set id
        setUserId(patient, "patient1");
        setUserId(caregiver, "caregiver1");

        booking = new Booking();
        booking.setPatient_id(patient.getId());
        booking.setCaregiver_id(caregiver.getId());
        booking.setStatus(BookingStatus.PENDING);
        booking.setStart_date_time(request.getStart_date_time());
        booking.setEnd_date_time(request.getEnd_date_time());
        booking.setSymptoms(request.getSymptoms());
        booking.setReason_for_visit(request.getReason_for_visit());
        booking.setNotes_from_patient(request.getNotes_from_patient());

        expectedPatchResponse = new PatchBookingResponse(
                "booking1",
                caregiver.getUsername(),
                BookingStatus.CANCELLED,
                booking.getStart_date_time(),
                booking.getEnd_date_time(),
                "Booking has been cancelled successfully"
        );
    }

    @Test
    void createBooking_shouldReturnBookingResponse() {
        // --- ARRANGE ---
        when(authService.getAuthenticated()).thenReturn(patient);
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingConverter.convertToCreateBookingResponse(savedBooking, caregiver)).thenReturn(expectedResponse);

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
    }

    @Test
    void cancelBooking_shouldReturnPatchBookingResponse_whenSuccessful() {

        // --- ARRANGE ---
        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(authService.getAuthenticated()).thenReturn(patient);
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingConverter.convertToPatchBookingResponse(booking, caregiver)).thenReturn(expectedPatchResponse);

        // --- ACT ---
        PatchBookingResponse actual = bookingService.cancelBooking("booking1");

        // --- ASSERT ---
        assertNotNull(actual);
        assertEquals(expectedPatchResponse, actual);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());

        // Verify interactions
        verify(bookingRepository).findById("booking1");
        verify(authService).getAuthenticated();
        verify(userRepository).findById(caregiver.getId());
        verify(bookingRepository).save(booking);
        verify(bookingConverter).convertToPatchBookingResponse(booking, caregiver);
    }

    @Test
    void cancelBooking_shouldThrowUnauthorizedException_ifPatientNotSameAsInBooking() {
        // --- ARRANGE ---
        booking.setPatient_id("otherPatientId");
        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(authService.getAuthenticated()).thenReturn(patient);

        // --- ACT & ASSERT ---
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> bookingService.cancelBooking("booking1")
        );

        assertEquals("You do not have permission to cancel this booking", exception.getMessage());
    }

    @Test
    void cancelBooking_shouldThrowResourceNotFoundException_ifBookingDoesNotExist() {
        // --- ARRANGE ---
        when(bookingRepository.findById("nonexistentBooking")).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.cancelBooking("nonexistentBooking")
        );

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void cancelBooking_shouldThrowResourceNotFoundException_ifCaregiverNotFound() {
        // --- ARRANGE ---
        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(authService.getAuthenticated()).thenReturn(patient);
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.empty()); // Caregiver saknas

        // --- ACT & ASSERT ---
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.cancelBooking("booking1")
        );

        assertEquals("Caregiver not found", exception.getMessage());
    }

    @Test
    void cancelBooking_shouldThrowConflictException_ifAlreadyCancelled() {

        // --- ARRANGE ---
        booking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(authService.getAuthenticated()).thenReturn(patient);

        // --- ACT ---
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> bookingService.cancelBooking("booking1"));

        // --- ASSERT ---
        assertEquals("This booking has already been cancelled", exception.getMessage());
    }

    @Test
    void cancelBooking_shouldThrowConflictException_ifBookingPassed() {

        // --- ARRANGE ---
        booking.setEnd_date_time(LocalDateTime.now().minusHours(1));
        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(authService.getAuthenticated()).thenReturn(patient);

        // --- ACT ---
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> bookingService.cancelBooking("booking1")
        );

        // --- ASSERT ---
        assertEquals("This booking has already passed", exception.getMessage());
    }

    @Test
    void cancelBooking_shouldThrowIllegalArgumentException_ifWithin24Hours() {

        // --- ARRANGE ---
        booking.setStart_date_time(LocalDateTime.now().plusHours(5));
        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(authService.getAuthenticated()).thenReturn(patient);

        // --- ACT ---
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking("booking1")
        );

        // --- ASSERT ---
        assertEquals("Booking cannot be cancelled within 24 hours of start time", exception.getMessage());
    }
}