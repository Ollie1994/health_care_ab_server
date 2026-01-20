package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.helpers.availability.AvailabilityHelper;
import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.AvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityHelperTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private AvailabilityHelper availabilityHelper;

    private User caregiver;

    @BeforeEach
    void setup() {
        caregiver = new User(
                "CAREGIVER_ID_123",
                "caregiver",
                "password",
                Set.of(Role.CAREGIVER)
        );
    }

    @Test
    void createAvailability_shouldCreateAndSaveAvailability() {
        // Arrange
        Availability savedAvailability = new Availability();
        savedAvailability.setCaregiverId(caregiver.getId());
        savedAvailability.setPeriods(new ArrayList<>());

        when(availabilityRepository.save(any(Availability.class)))
                .thenReturn(savedAvailability);

        // Act
        Availability result = availabilityHelper.createAvailability(caregiver);

        // Assert
        assertNotNull(result);
        assertEquals(caregiver.getId(), result.getCaregiverId());
        assertTrue(result.getPeriods().isEmpty());

        verify(availabilityRepository).save(any(Availability.class));
    }

    @Test
    void updateAvailability_shouldUpdateAndSaveAvailability() {
        // Arrange
        Availability availability = new Availability();
        List<String> periodIds = List.of("period_1", "period_2");

        Availability savedAvailability = new Availability();
        savedAvailability.setCaregiverId(caregiver.getId());
        savedAvailability.setPeriods(periodIds);

        when(availabilityRepository.save(availability))
                .thenReturn(savedAvailability);

        // Act
        Availability result =
                availabilityHelper.updateAvailability(availability, periodIds, caregiver);

        // Assert
        assertNotNull(result);
        assertEquals(caregiver.getId(), result.getCaregiverId());
        assertEquals(2, result.getPeriods().size());
        assertEquals(periodIds, result.getPeriods());

        verify(availabilityRepository).save(availability);
    }
}
