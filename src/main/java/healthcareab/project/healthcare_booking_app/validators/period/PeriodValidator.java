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


    public Period validateUpdatePeriod(UpdateAvailabilityRequest request, List<String> periodIds) {
        Period newPeriod = request.getNewPeriod();
        LocalDate today = LocalDate.now();


        if (periodIds != null && !periodIds.isEmpty() && periodIds.get(0) != null) {

            System.out.println("INSIDE " + periodIds);
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

                if (newPeriod.getStartDateTime().equals(start) || newPeriod.getEndDateTime().equals(end)) {
                    throw new IllegalArgumentException("Period already exists");
                }
                // FIX THIS , throws even if the period is on other day
                if (Duration.between(end, newPeriod.getStartDateTime()).toMinutes() < 10 || Duration.between(start, newPeriod.getEndDateTime()).toMinutes() < 10) {
                    throw new IllegalArgumentException("NewPeriod cant be scheduled within 10 minutes of another meeting");
                }
                if (newPeriod.getStartDateTime().isBefore(start) && newPeriod.getEndDateTime().isBefore(end) && newPeriod.getEndDateTime().isAfter(start)) {
                    throw new IllegalArgumentException("NewPeriod cant be within an already existing period");
                }
                if (newPeriod.getStartDateTime().isAfter(start) && newPeriod.getEndDateTime().isAfter(end) && newPeriod.getStartDateTime().isBefore(end)) {
                    throw new IllegalArgumentException("NewPeriod cant be within an already existing period");
                }
            }
        }




        // Not working ?????
        if (newPeriod.getStartDateTime().toLocalTime().isAfter(LocalTime.NOON) && newPeriod.getStartDateTime().toLocalTime().isBefore(LocalTime.of(13, 0))
                || newPeriod.getEndDateTime().toLocalTime().isAfter(LocalTime.NOON) && newPeriod.getEndDateTime().toLocalTime().isBefore(LocalTime.of(13, 0))) {
            throw new IllegalArgumentException("Cannot set period during lunch (12:00–13:00)");
        }
        Duration duration = Duration.between(newPeriod.getStartDateTime(), newPeriod.getEndDateTime());
        if (duration.toHours() > 1) {
            throw new IllegalArgumentException("NewPeriod cant longer than 1 hour");
        }
        if (duration.toHours() < 1) {
            throw new IllegalArgumentException("NewPeriod cant shorter than 1 hour");
        }
        if (request.getNewPeriod() == null || request.getNewPeriod().getEndDateTime() == null || request.getNewPeriod().getStartDateTime() == null) {
            throw new IllegalArgumentException("New period cant have null values");
        }
        if (newPeriod.getStartDateTime().toLocalDate().isBefore(today) || newPeriod.getEndDateTime().toLocalDate().isBefore(today)) {
            throw new IllegalArgumentException("Period cant be before today's date");
        }
        if (newPeriod.getStartDateTime().isAfter(newPeriod.getEndDateTime())) {
            throw new IllegalArgumentException("New period start date should be before end date");
        }
        if (newPeriod.getStartDateTime().isEqual(newPeriod.getEndDateTime())) {
            throw new IllegalArgumentException("New period start date cant be equal to end date");
        }
        java.time.Period result = java.time.Period.between(today, newPeriod.getStartDateTime().toLocalDate());
        if (result.getDays() > 28) {
            throw new IllegalArgumentException("New period cant be added that is more than 28 days in the future");
        }

        return newPeriod;
    }

}
