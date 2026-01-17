package healthcareab.project.healthcare_booking_app.helpers.period;

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
public class PeriodHelper {
    private final PeriodRepository periodRepository;

    public PeriodHelper(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }


    public void deletePeriod(String id) {
        periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Period not found"));
        periodRepository.deleteById(id);
    }

    public String updatePeriods(UpdateAvailabilityRequest request, List<String> periodIds) {

        Period newPeriod = request.getNewPeriod();
        LocalDate today = LocalDate.now();

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
            throw new IllegalArgumentException("New period is required");
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


//        System.out.println("Before formatting: " + today);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String formattedToday = today.format(formatter);
//        String formattedStartDate = newPeriod.getStartDateTime().format(formatter);
//        String formattedEndDate = newPeriod.getEndDateTime().format(formatter);
//        System.out.println("After formatting: " + formattedToday + ",  " + formattedStartDate + " - " + formattedEndDate);
//

        java.time.Period result = java.time.Period.between(today, newPeriod.getStartDateTime().toLocalDate());
        if (result.getDays() > 28) {
            throw new IllegalArgumentException("New period cant be added that is more than 28 days in the future");
        }

        // 1 timme period - 10 min break. 60 min lunch 12-13

        Period savedperiod = periodRepository.save(newPeriod);

        return savedperiod.getId();
    }


    // create new period kanske är bättre namn än updatePeriods ????

    // getMy avail -> get myPeriods
    // getAvail -> get periods by id


}
