package healthcareab.project.healthcare_booking_app.validators.period;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.repository.PeriodRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PeriodValidator {
    private final PeriodRepository periodRepository;

    public PeriodValidator(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }


    public void validateUpdatePeriod(UpdateAvailabilityRequest request, List<String> periodIds) {

        LocalDate today = LocalDate.now();

        if (periodIds != null && !periodIds.isEmpty() && periodIds.get(0) != null) {

            List<Period> periods = new ArrayList<>();
            Period period;

            for (String id : periodIds) {
                period = periodRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Period not found"));
                periods.add(period);
            }

            for (Period p : periods) {
                LocalDateTime start = p.getStartDateTime();
                LocalDateTime end = p.getEndDateTime();
                if (request.getStartDateTime().equals(start) || request.getEndDateTime().equals(end)) {
                    throw new IllegalArgumentException("Period already exists");
                }
                if (Duration.between(end, request.getStartDateTime()).abs().toMinutes() < 10
                        || Duration.between(start, request.getEndDateTime()).abs().toMinutes() < 10) {
                    throw new IllegalArgumentException("NewPeriod cant be scheduled within 10 minutes of another meeting");
                }
                if (request.getStartDateTime().isBefore(start) && request.getEndDateTime().isBefore(end) && request.getEndDateTime().isAfter(start)) {
                    throw new IllegalArgumentException("NewPeriod cant be within an already existing period");
                }
                if (request.getStartDateTime().isAfter(start) && request.getEndDateTime().isAfter(end) && request.getStartDateTime().isBefore(end)) {
                    throw new IllegalArgumentException("NewPeriod cant be within an already existing period");
                }
            }
        }

        LocalTime start = request.getStartDateTime().toLocalTime();
        LocalTime end = request.getEndDateTime().toLocalTime();
        Duration duration = Duration.between(request.getStartDateTime(), request.getEndDateTime());

        if (start.compareTo(LocalTime.NOON) >= 0 && start.compareTo(LocalTime.of(13, 0)) < 0) {
            throw new IllegalArgumentException("Start time cannot be during lunch (12:00–13:00)");
        }
        if (end.compareTo(LocalTime.NOON) > 0 && end.compareTo(LocalTime.of(13, 0)) < 0) {
            throw new IllegalArgumentException("End time cannot be during lunch (12:00–13:00)");
        }
        if (duration.toMinutes() > 60) {
            throw new IllegalArgumentException("NewPeriod cant be longer than 1 hour");
        }
        if (duration.toMinutes() < 60) {
            throw new IllegalArgumentException("NewPeriod cant be shorter than 1 hour");
        }
        if (request.getEndDateTime() == null || request.getStartDateTime() == null) {
            throw new IllegalArgumentException("New period cant have null values");
        }
        if (request.getStartDateTime().toLocalDate().isBefore(today) || request.getEndDateTime().toLocalDate().isBefore(today)) {
            throw new IllegalArgumentException("Period cant be before today's date");
        }
        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new IllegalArgumentException("New period start date should be before end date");
        }
        if (request.getStartDateTime().isEqual(request.getEndDateTime())) {
            throw new IllegalArgumentException("New period start date cant be equal to end date");
        }
        java.time.Period result = java.time.Period.between(today, request.getStartDateTime().toLocalDate());
        if (result.getDays() > 28) {
            throw new IllegalArgumentException("New period cant be added that is more than 28 days in the future");
        }
    }

}
