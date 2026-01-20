package healthcareab.project.healthcare_booking_app.helpers.period;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.repository.PeriodRepository;
import healthcareab.project.healthcare_booking_app.validators.period.PeriodValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PeriodHelper {
    private final PeriodRepository periodRepository;
    private final PeriodValidator periodValidator;

    public PeriodHelper(PeriodRepository periodRepository, PeriodValidator periodValidator) {
        this.periodRepository = periodRepository;
        this.periodValidator = periodValidator;
    }


    public void deletePeriod(String id) {
        periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Period not found"));
        periodRepository.deleteById(id);
    }

    public String createPeriod(UpdateAvailabilityRequest request, List<String> periodIds) {

        periodValidator.validateUpdatePeriod(request, periodIds);

        Period newPeriod = new Period();

        newPeriod.setStartDateTime(request.getStartDateTime());
        newPeriod.setEndDateTime(request.getEndDateTime());

        Period savedperiod = periodRepository.save(newPeriod);

        return savedperiod.getId();
    }
}
