package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingHistoryResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingsResponse;
import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.helpers.email.SESEmailHelper;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.Role;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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
        patient = new User("PATIENT_ID_123","patient", "password", null);
        caregiver = new User("CAREGIVER_ID_123","caregiver", "password", null);


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
    /*--------------------
    POSITIVE SCENARIOS
    --------------------*/
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

    @Test
    void getMyBookings_whenUserIsPatient_shouldReturnGetBookingResponse() {
        // --- ARRANGE ---
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setRoles(Set.of(Role.PATIENT));

        caregiver.setFirstName("Dr");
        caregiver.setLastName("McCaregiver");
        caregiver.setRoles(Set.of(Role.CAREGIVER));

        Booking bookingOne = new Booking("BOOKING_ID_1");
        bookingOne.setPatientId(patient.getId());
        bookingOne.setCaregiverId(caregiver.getId());
        bookingOne.setStartDateTime(LocalDateTime.of(2026, 8, 5, 10, 0));
        bookingOne.setEndDateTime(LocalDateTime.of(2026, 8, 5, 11, 0));
        bookingOne.setStatus(BookingStatus.CONFIRMED);
        bookingOne.setSymptoms(List.of("Flu", "Cough"));

        Booking bookingTwo = new Booking("BOOKING_ID_2");
        bookingTwo.setPatientId(patient.getId());
        bookingTwo.setCaregiverId(caregiver.getId());
        bookingTwo.setStartDateTime(LocalDateTime.of(2026, 8, 6, 14, 0));
        bookingTwo.setEndDateTime(LocalDateTime.of(2026, 8, 6, 15, 30));
        bookingTwo.setStatus(BookingStatus.CONFIRMED);
        bookingTwo.setSymptoms(List.of("Headache", "Fatigue"));

        when(authService.getAuthenticated()).thenReturn(patient);
        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(bookingRepository.findByPatientId(patient.getId())).thenReturn(List.of(bookingOne, bookingTwo));
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

        when(bookingConverter.convertToGetBookingsResponse(any(Booking.class), anyString()))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    String fullName = invocation.getArgument(1);
                    return new GetBookingsResponse(
                            booking.getStartDateTime(),
                            booking.getEndDateTime(),
                            booking.getStatus(),
                            fullName,
                            booking.getSymptoms(),
                            booking.getId()
                    );
                });

        // --- ACT ---
        List<GetBookingsResponse> result = bookingService.getMyBookings();

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(bookingRepository).findByPatientId(patient.getId());
        verify(userRepository).findById(patient.getId());
        verify(userRepository, times(2)).findById(caregiver.getId());
        verify(bookingConverter, times(2))
                .convertToGetBookingsResponse(any(Booking.class), eq("Dr McCaregiver"));
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingRepository, never()).findByCaregiverId(any());
    }

    @Test
    void getMyBookings_whenUserIsCaregiver_shouldReturnGetBookingResponse() {
        // --- ARRANGE ---
        caregiver.setFirstName("Dr");
        caregiver.setLastName("McCaregiver");
        caregiver.setRoles(Set.of(Role.CAREGIVER));

        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setRoles(Set.of(Role.PATIENT));

        Booking bookingOne = new Booking("BOOKING_ID_1");
        bookingOne.setPatientId(patient.getId());
        bookingOne.setCaregiverId(caregiver.getId());
        bookingOne.setStartDateTime(LocalDateTime.of(2026, 8, 5, 10, 0));
        bookingOne.setEndDateTime(LocalDateTime.of(2026, 8, 5, 11, 0));
        bookingOne.setStatus(BookingStatus.CONFIRMED);
        bookingOne.setSymptoms(List.of("Flu", "Cough"));

        Booking bookingTwo = new Booking("BOOKING_ID_2");
        bookingTwo.setPatientId(patient.getId());
        bookingTwo.setCaregiverId(caregiver.getId());
        bookingTwo.setStartDateTime(LocalDateTime.of(2026, 8, 6, 14, 0));
        bookingTwo.setEndDateTime(LocalDateTime.of(2026, 8, 6, 15, 30));
        bookingTwo.setStatus(BookingStatus.CONFIRMED);
        bookingTwo.setSymptoms(List.of("Headache", "Fatigue"));

        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));
        when(bookingRepository.findByCaregiverId(caregiver.getId())).thenReturn(List.of(bookingOne, bookingTwo));
        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        when(bookingConverter.convertToGetBookingsResponse(any(Booking.class), anyString()))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    String fullName = invocation.getArgument(1);
                    return new GetBookingsResponse(
                            booking.getStartDateTime(),
                            booking.getEndDateTime(),
                            booking.getStatus(),
                            fullName,
                            booking.getSymptoms(),
                            booking.getId()
                    );
                });

        // --- ACT ---
        List<GetBookingsResponse> result = bookingService.getMyBookings();

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(2, result.size());

        // --- VERIFY branch behavior ---
        verify(bookingRepository).findByCaregiverId(caregiver.getId());
        verify(userRepository).findById(caregiver.getId());
        verify(userRepository, times(2)).findById(patient.getId()); // once per booking
        verify(bookingConverter, times(2))
                .convertToGetBookingsResponse(any(Booking.class), eq("John Doe"));
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingRepository, never()).findByPatientId(any());
    }

    @Test
    void getMyBookingHistory_whenUserIsPatient_shouldReturnGetBookingHistory() {
        // --- ARRANGE ---
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setRoles(Set.of(Role.PATIENT));

        caregiver.setFirstName("Dr");
        caregiver.setLastName("McCaregiver");
        caregiver.setRoles(Set.of(Role.CAREGIVER));

        Booking bookingOne = new Booking("BOOKING_ID_1");
        bookingOne.setPatientId(patient.getId());
        bookingOne.setCaregiverId(caregiver.getId());
        bookingOne.setStartDateTime(LocalDateTime.now().minusDays(2));
        bookingOne.setEndDateTime(LocalDateTime.now().minusDays(1));
        bookingOne.setStatus(BookingStatus.CONFIRMED);
        bookingOne.setSymptoms(List.of("Flu", "Cough"));

        Booking bookingTwo = new Booking("BOOKING_ID_2");
        bookingTwo.setPatientId(patient.getId());
        bookingTwo.setCaregiverId(caregiver.getId());
        bookingTwo.setStartDateTime(LocalDateTime.now().minusDays(3));
        bookingTwo.setEndDateTime(LocalDateTime.now().minusDays(2));
        bookingTwo.setStatus(BookingStatus.CONFIRMED);
        bookingTwo.setSymptoms(List.of("Headache", "Fatigue"));

        when(authService.getAuthenticated()).thenReturn(patient);
        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        when(bookingRepository.findByPatientIdAndEndDateTimeBefore(eq(patient.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingOne, bookingTwo));
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

        when(bookingConverter.convertToGetBookingHistoryResponse(any(Booking.class), anyString()))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    String fullName = invocation.getArgument(1);
                    return new GetBookingHistoryResponse(
                            booking.getStartDateTime(),
                            fullName,
                            booking.getId()
                    );
                });

        // --- ACT ---
        List<GetBookingHistoryResponse> result = bookingService.getMyBookingHistory();

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(bookingRepository).findByPatientIdAndEndDateTimeBefore(eq(patient.getId()), any(LocalDateTime.class));
        verify(userRepository).findById(patient.getId());
        verify(userRepository, times(2)).findById(caregiver.getId());
        verify(bookingConverter, times(2))
                .convertToGetBookingHistoryResponse(any(Booking.class), eq("Dr McCaregiver"));
        verify(bookingRepository, never()).findByCaregiverIdAndEndDateTimeBefore(any(), any());
    }

    @Test
    void getMyBookingHistory_whenUserIsCaregiver_shouldReturnGetBookingHistory() {
        // --- ARRANGE ---
        caregiver.setFirstName("Dr");
        caregiver.setLastName("McCaregiver");
        caregiver.setRoles(Set.of(Role.CAREGIVER));

        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setRoles(Set.of(Role.PATIENT));

        Booking bookingOne = new Booking("BOOKING_ID_1");
        bookingOne.setPatientId(patient.getId());
        bookingOne.setCaregiverId(caregiver.getId());
        bookingOne.setStartDateTime(LocalDateTime.now().minusDays(2));
        bookingOne.setEndDateTime(LocalDateTime.now().minusDays(1));
        bookingOne.setStatus(BookingStatus.CONFIRMED);
        bookingOne.setSymptoms(List.of("Flu", "Cough"));

        Booking bookingTwo = new Booking("BOOKING_ID_2");
        bookingTwo.setPatientId(patient.getId());
        bookingTwo.setCaregiverId(caregiver.getId());
        bookingTwo.setStartDateTime(LocalDateTime.now().minusDays(3));
        bookingTwo.setEndDateTime(LocalDateTime.now().minusDays(2));
        bookingTwo.setStatus(BookingStatus.CONFIRMED);
        bookingTwo.setSymptoms(List.of("Headache", "Fatigue"));

        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(userRepository.findById(caregiver.getId())).thenReturn(Optional.of(caregiver));

        when(bookingRepository.findByCaregiverIdAndEndDateTimeBefore(eq(caregiver.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingOne, bookingTwo));
        when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        when(bookingConverter.convertToGetBookingHistoryResponse(any(Booking.class), anyString()))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    String fullName = invocation.getArgument(1);
                    return new GetBookingHistoryResponse(
                            booking.getStartDateTime(),
                            fullName,
                            booking.getId()
                    );
                });

        // --- ACT ---
        List<GetBookingHistoryResponse> result = bookingService.getMyBookingHistory();

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(bookingRepository).findByCaregiverIdAndEndDateTimeBefore(eq(caregiver.getId()), any(LocalDateTime.class));
        verify(userRepository).findById(caregiver.getId());
        verify(userRepository, times(2)).findById(patient.getId());
        verify(bookingConverter, times(2))
                .convertToGetBookingHistoryResponse(any(Booking.class), eq("John Doe"));
        verify(bookingRepository, never()).findByPatientIdAndEndDateTimeBefore(any(), any());
    }

    /*--------------------
    NEGATIVE SCENARIOS
    --------------------*/
    @Test
    void getMyBookings_whenUserHasNoRoles_shouldThrowAccessDeniedException() {
        // --- ARRANGE ---
        User noRoleUser = new User("USER_ID_1", "username", "password", Set.of());

        when(authService.getAuthenticated()).thenReturn(noRoleUser);
        when(userRepository.findById(noRoleUser.getId())).thenReturn(Optional.of(noRoleUser));

        // --- ACT & ASSERT ---
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> bookingService.getMyBookings()
        );

        assertEquals("You are not authorized to view bookings", exception.getMessage());

        // --- VERIFY ---
        verify(bookingRepository, never()).findByPatientId(any());
        verify(bookingRepository, never()).findByCaregiverId(any());
        verify(bookingConverter, never()).convertToGetBookingsResponse(any(), anyString());
    }

    @Test
    void getMyBookingHistory_whenUserHasNoRoles_shouldThrowAccessDeniedException() {
        // --- ARRANGE ---
        User noRoleUser = new User("USER_ID_1", "username", "password", Set.of());

        when(authService.getAuthenticated()).thenReturn(noRoleUser);
        when(userRepository.findById(noRoleUser.getId())).thenReturn(Optional.of(noRoleUser));

        // --- ACT & ASSERT ---
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> bookingService.getMyBookingHistory()
        );

        assertEquals("You are not authorized to view bookings", exception.getMessage());

        // --- VERIFY ---
        verify(bookingRepository, never()).findByPatientIdAndEndDateTimeBefore(any(), any());
        verify(bookingRepository, never()).findByCaregiverIdAndEndDateTimeBefore(any(), any());
        verify(bookingConverter, never()).convertToGetBookingHistoryResponse(any(), anyString());
    }
}