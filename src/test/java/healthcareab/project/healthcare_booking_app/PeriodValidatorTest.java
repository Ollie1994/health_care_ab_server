package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.repository.PeriodRepository;
import healthcareab.project.healthcare_booking_app.validators.period.PeriodValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodValidatorTest {

    @Mock
    private PeriodRepository periodRepository;

    @InjectMocks
    private PeriodValidator periodValidator;

    private LocalDateTime baseStart;
    private LocalDateTime baseEnd;

    @BeforeEach
    void setup() {
        baseStart = LocalDate.now().plusDays(1).atTime(9, 0);
        baseEnd = baseStart.plusHours(1);
    }

    // ---------- ✅ POSITIVE ----------

    @Test
    void validateUpdatePeriod_shouldPass_whenValid() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest("CAREGIVER_ID", baseStart, baseEnd);

        List<String> periodIds = List.of();
        assertDoesNotThrow(() ->
                periodValidator.validateUpdatePeriod(request, periodIds));
    }

    // ---------- ❌ NEGATIVES ----------

    @Test
    void shouldThrow_whenNullValues() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest("CAREGIVER_ID", baseStart, null);

        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenStartAfterEnd() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        baseEnd.plusHours(1),
                        baseEnd
                );
        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenStartEqualsEnd() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest("CAREGIVER_ID", baseStart, baseStart);

        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenDateBeforeToday() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().minusDays(1).plusHours(1)
                );

        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenMoreThan28DaysAhead() {
        LocalDateTime start = LocalDate.now().plusDays(29).atTime(9, 0);

        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        start,
                        start.plusHours(1)
                );

        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenLongerThanOneHour() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        baseStart,
                        baseStart.plusMinutes(61)
                );

        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenShorterThanOneHour() {
        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        baseStart,
                        baseStart.plusMinutes(30)
                );
        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenStartDuringLunch() {
        LocalDateTime lunchStart = baseStart.withHour(12);

        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        lunchStart,
                        lunchStart.plusHours(1)
                );
        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenEndDuringLunch() {
        LocalDateTime start = baseStart.withHour(11);

        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest(
                        "CAREGIVER_ID",
                        start,
                        start.plusHours(1).withHour(12).plusMinutes(30)
                );

        List<String> periodIds = List.of(); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));
    }

    @Test
    void shouldThrow_whenPeriodTooCloseToExisting() {
        Period existing = new Period();
        existing.setStartDateTime(baseStart.minusMinutes(5));
        existing.setEndDateTime(baseEnd.minusMinutes(5));

        when(periodRepository.findById("p1"))
                .thenReturn(Optional.of(existing));

        UpdateAvailabilityRequest request =
                new UpdateAvailabilityRequest("CAREGIVER_ID", baseStart, baseEnd);

        List<String> periodIds = List.of("p1"); // moved outside

        assertThrows(IllegalArgumentException.class,
                () -> periodValidator.validateUpdatePeriod(request, periodIds));



    }
}


