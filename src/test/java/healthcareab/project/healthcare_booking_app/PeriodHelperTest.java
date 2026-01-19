package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.helpers.period.PeriodHelper;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.repository.PeriodRepository;
import healthcareab.project.healthcare_booking_app.validators.period.PeriodValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeriodHelperTest {

    @Mock
    private PeriodRepository periodRepository;

    @Mock
    private PeriodValidator periodValidator;

    @InjectMocks
    private PeriodHelper periodHelper;

    @Test
    void deletePeriod_shouldDelete_whenPeriodExists() {
        // Arrange
        Period period = new Period("period_1");

        when(periodRepository.findById("period_1"))
                .thenReturn(Optional.of(period));

        // Act
        periodHelper.deletePeriod("period_1");

        // Assert
        verify(periodRepository).deleteById("period_1");
    }

    @Test
    void deletePeriod_shouldThrow_whenPeriodNotFound() {
        // Arrange
        when(periodRepository.findById("missing_id"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> periodHelper.deletePeriod("missing_id"));

        verify(periodRepository, never()).deleteById(any());
    }

    @Test
    void createPeriod_shouldValidateAndSavePeriod() {
        // Arrange
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        LocalDateTime.of(2026, 1, 20, 8, 0),
                        LocalDateTime.of(2026, 1, 20, 9, 0)
                );

        Period savedPeriod = new Period("new_period_id");


        when(periodRepository.save(any(Period.class)))
                .thenReturn(savedPeriod);

        // Act
        String result = periodHelper.createPeriod(request, List.of());

        // Assert
        assertEquals("new_period_id", result);

        verify(periodValidator)
                .validateUpdatePeriod(eq(request), anyList());
        verify(periodRepository).save(any(Period.class));
    }
}
