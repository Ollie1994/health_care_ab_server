package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.converters.AvailabilityConverter;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.exceptions.ConflictException;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.helpers.availability.AvailabilityHelper;
import healthcareab.project.healthcare_booking_app.helpers.period.PeriodHelper;
import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.AvailabilityRepository;
import healthcareab.project.healthcare_booking_app.repository.PeriodRepository;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import healthcareab.project.healthcare_booking_app.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AvailabilityConverter availabilityConverter;

    @Mock
    private AvailabilityHelper availabilityHelper;

    @Mock
    private PeriodHelper periodHelper;

    @Mock
    private PeriodRepository periodRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private User caregiver;
    private UpdateAvailabilityResponse updateAvailabilityResponse;
    private Availability availability;
    private Availability updatedAvailability;

    @BeforeEach
    void setup() {

        caregiver = new User("CAREGIVER_ID_123", "caregiver", "password", Set.of(Role.CAREGIVER));

        availability = new Availability();
        availability.setCaregiverId(caregiver.getId());
        availability.setPeriods(new ArrayList<>());

        updatedAvailability = new Availability();
        updatedAvailability.setCaregiverId(caregiver.getId());
        updatedAvailability.setPeriods(List.of("old_id_1", "new_id_2"));

        updateAvailabilityResponse = new UpdateAvailabilityResponse("Availability has been updated successfully");
    }

    @Test
    void updateAvailability_shouldReturn_updateAvailabilityResponse() {

        // Arrange
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest("CAREGIVER_ID_123", LocalDateTime.of(2026, 1, 20, 8, 0),
                LocalDateTime.of(2026, 1, 20, 9, 0));
        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(availabilityRepository.findByCaregiverId(caregiver.getId())).thenReturn(Optional.of(availability));
        when(periodHelper.createPeriod(request, availability.getPeriods())).thenReturn("old_id_1");
        availability.getPeriods().add("new_id_2");
        when(availabilityHelper.updateAvailability(availability, availability.getPeriods(), caregiver)).thenReturn(updatedAvailability);
        when(availabilityConverter.convertToUpdateAvailabilityResponse(updatedAvailability)).thenReturn(updateAvailabilityResponse);

        // Act
        UpdateAvailabilityResponse result = availabilityService.updateAvailability(request);

        // Assert
        assertNotNull(result);
        assertEquals(result.getMessage(), updateAvailabilityResponse.getMessage());
        assertEquals(caregiver.getId(), request.getCaregiverId());
        assertEquals(2, updatedAvailability.getPeriods().size());

        verify(availabilityRepository, times(1)).findByCaregiverId(caregiver.getId());
        verify(availabilityConverter, times(1)).convertToUpdateAvailabilityResponse(updatedAvailability);
        verify(periodHelper, times(1)).createPeriod(
                eq(request),
                argThat(list -> list.containsAll(List.of("old_id_1", "new_id_2")) && list.size() == 2)
        );
    }

    @Test
    void getMyAvailability_shouldReturnPeriods_forCaregiver() {
        // Arrange
        Period period1 = new Period();
        Period period2 = new Period();

        availability.setPeriods(List.of("old_id_1", "new_id_2"));

        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(availabilityRepository.findByCaregiverId(caregiver.getId()))
                .thenReturn(Optional.of(availability));
        when(periodRepository.findById("old_id_1"))
                .thenReturn(Optional.of(period1));
        when(periodRepository.findById("new_id_2"))
                .thenReturn(Optional.of(period2));

        // Act
        List<Period> result = availabilityService.getMyAvailability();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(period1));
        assertTrue(result.contains(period2));

        verify(authService).getAuthenticated();
        verify(availabilityRepository).findByCaregiverId(caregiver.getId());
        verify(periodRepository).findById("old_id_1");
        verify(periodRepository).findById("new_id_2");
    }

    @Test
    void deleteAvailabilityPeriodById_shouldDeletePeriod() {
        // Arrange
        availability.setPeriods(new ArrayList<>(List.of("period_1", "period_2")));

        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(availabilityRepository.findByCaregiverId(caregiver.getId()))
                .thenReturn(Optional.of(availability));

        // Act
        availabilityService.deleteAvailabilityPeriodById("period_1");

        // Assert
        assertFalse(availability.getPeriods().contains("period_1"));

        verify(periodHelper).deletePeriod("period_1");
        verify(availabilityRepository).save(availability);
    }


    // negative tests
    @Test
    void getMyAvailability_shouldThrowAccessDenied_whenNotCaregiverRole() {
        caregiver.setRoles(Set.of(Role.PATIENT));
        when(authService.getAuthenticated()).thenReturn(caregiver);

        assertThrows(AccessDeniedException.class,
                () -> availabilityService.getMyAvailability());
    }

    @Test
    void updateAvailability_shouldThrow_ConflictException_whenAuthenticatedIdDoesNotMatchRequest() {
        // Arrange
        User authenticatedUser = new User("AUTH_ID");
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "DIFFERENT_ID", // mismatching caregiverId
                        LocalDateTime.now().plusDays(1).withHour(9),
                        LocalDateTime.now().plusDays(1).withHour(10)
                );

        when(authService.getAuthenticated()).thenReturn(authenticatedUser);

        // Act + Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> availabilityService.updateAvailability(request));

        assertEquals("Conflicting ids", exception.getMessage());
    }

    @Test
    void getMyAvailability_shouldThrow_whenPeriodNotFound() {
        availability.setPeriods(List.of("missing_id"));

        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(availabilityRepository.findByCaregiverId(caregiver.getId()))
                .thenReturn(Optional.of(availability));
        when(periodRepository.findById("missing_id"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> availabilityService.getMyAvailability());
    }

    @Test
    void deleteAvailabilityPeriodById_shouldThrow_whenPeriodNotInAvailability() {
        // Arrange
        availability.setPeriods(new ArrayList<>(List.of("period_1")));

        when(authService.getAuthenticated()).thenReturn(caregiver);
        when(availabilityRepository.findByCaregiverId(caregiver.getId()))
                .thenReturn(Optional.of(availability));

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> availabilityService.deleteAvailabilityPeriodById("missing_period"));

        verify(periodHelper, never()).deletePeriod(any());
        verify(availabilityRepository, never()).save(any());
    }



}
