package healthcareab.project.healthcare_booking_app.services;

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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {

    private final AuthService authService;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityConverter availabilityConverter;
    private final AvailabilityHelper availabilityHelper;
    private final PeriodHelper periodHelper;
    private final PeriodRepository periodRepository;


    public AvailabilityService(AuthService authService, AvailabilityRepository availabilityRepository, AvailabilityConverter availabilityConverter, AvailabilityHelper availabilityHelper, PeriodHelper periodHelper, PeriodRepository periodRepository) {
        this.authService = authService;
        this.availabilityRepository = availabilityRepository;
        this.availabilityConverter = availabilityConverter;
        this.availabilityHelper = availabilityHelper;
        this.periodHelper = periodHelper;
        this.periodRepository = periodRepository;
    }


    public UpdateAvailabilityResponse updateAvailability(UpdateAvailabilityRequest request) {

        User caregiver = authService.getAuthenticated();

        if (!caregiver.getId().equals(request.getCaregiverId()) && request.getCaregiverId() != null) {
            throw new ConflictException("Conflicting ids");
        }
        if (!caregiver.getRoles().contains(Role.CAREGIVER)) {
            throw new AccessDeniedException("Access denied");
        }
        if(request.getNewPeriod() == null || request.getNewPeriod().getEndDateTime() == null || request.getNewPeriod().getStartDateTime() == null) {
            throw new IllegalArgumentException("New period is required");
        }

        Availability availability = availabilityRepository.findByCaregiverId(caregiver.getId()).orElse(availabilityHelper.createAvailability(request));

        String periodId = periodHelper.updatePeriods(request);
        List<String> updatedPeriodIds = availability.getPeriods();
        updatedPeriodIds.add(periodId);

        availability.setCaregiverId(request.getCaregiverId());
        availability.setPeriods(updatedPeriodIds);

        Availability updatedAvailability = availabilityRepository.save(availability);
        return availabilityConverter.convertToUpdateAvailabilityResponse(updatedAvailability);
    }


    public List<Period> getMyAvailability() {
        User user = authService.getAuthenticated();
        Availability availability = availabilityRepository.findByCaregiverId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));
        Period period;
        List<Period> periods = new ArrayList<>();
        for (String id : availability.getPeriods()) {
            period = periodRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Period not found"));
            periods.add(period);
        }
        return periods;
    }

    public void deleteAvailabilityPeriodById(String id) {
        User user = authService.getAuthenticated();
        periodHelper.deletePeriod(id);
        Availability availability = availabilityRepository.findByCaregiverId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));
        availability.getPeriods().remove(id);
        availabilityRepository.save(availability);

    }
}
