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
import java.time.temporal.ChronoUnit;
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

        // Standard format validation
        if (request.getEndDateTime() == null || request.getStartDateTime() == null || request.getCaregiverId() == null) {
            throw new IllegalArgumentException("Request cant have null values");
        }
        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new IllegalArgumentException("Period start date should be before end date");
        }
        if (request.getStartDateTime().isEqual(request.getEndDateTime())) {
            throw new IllegalArgumentException("Period start date cant be equal to end date");
        }
        if (request.getStartDateTime().toLocalDate().isBefore(today) || request.getEndDateTime().toLocalDate().isBefore(today)) {
            throw new IllegalArgumentException("Period cant be before today's date");
        }

        // Validation for how far ahead you can set availability
        long daysAhead = ChronoUnit.DAYS.between(today, request.getStartDateTime().toLocalDate());
        if (daysAhead > 28) {
            throw new IllegalArgumentException("Period cant be added that is more than 28 days in the future");
        }

        // Validation for availability length
        Duration duration = Duration.between(request.getStartDateTime(), request.getEndDateTime());
        if (duration.toMinutes() > 60) {
            throw new IllegalArgumentException("Period cant be longer than 1 hour");
        }
        if (duration.toMinutes() < 60) {
            throw new IllegalArgumentException("Period cant be shorter than 1 hour");
        }

        // validation for lunch time
        LocalTime start = request.getStartDateTime().toLocalTime();
        LocalTime end = request.getEndDateTime().toLocalTime();
        if (start.compareTo(LocalTime.NOON) >= 0 && start.compareTo(LocalTime.of(13, 0)) < 0) {
            throw new IllegalArgumentException("Start time cannot be during lunch (12:00–13:00)");
        }
        if (end.compareTo(LocalTime.NOON) > 0 && end.compareTo(LocalTime.of(13, 0)) < 0) {
            throw new IllegalArgumentException("End time cannot be during lunch (12:00–13:00)");
        }


        // Validate new period against existing periods
        if (periodIds != null && !periodIds.isEmpty() && periodIds.get(0) != null) {

            List<Period> periods = new ArrayList<>();
            Period period;

            for (String id : periodIds) {
                period = periodRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Period not found"));
                periods.add(period);
            }

            for (Period p : periods) {
                LocalDateTime startDateTime = p.getStartDateTime();
                LocalDateTime endDateTime = p.getEndDateTime();
                if (request.getStartDateTime().equals(startDateTime) || request.getEndDateTime().equals(endDateTime)) {
                    throw new IllegalArgumentException("Period already exists");
                }
                if (Duration.between(endDateTime, request.getStartDateTime()).abs().toMinutes() < 10
                        || Duration.between(startDateTime, request.getEndDateTime()).abs().toMinutes() < 10) {
                    throw new IllegalArgumentException("Period cant be scheduled within 10 minutes of another meeting");
                }
                if (request.getStartDateTime().isBefore(startDateTime) && request.getEndDateTime().isBefore(endDateTime) && request.getEndDateTime().isAfter(startDateTime)) {
                    throw new IllegalArgumentException("Period cant be within an already existing period");
                }
                if (request.getStartDateTime().isAfter(startDateTime) && request.getEndDateTime().isAfter(endDateTime) && request.getStartDateTime().isBefore(endDateTime)) {
                    throw new IllegalArgumentException("Period cant be within an already existing period");
                }
            }
        }
    }


}
