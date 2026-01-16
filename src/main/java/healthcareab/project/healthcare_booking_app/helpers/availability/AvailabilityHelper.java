package healthcareab.project.healthcare_booking_app.helpers.availability;

import healthcareab.project.healthcare_booking_app.converters.AvailabilityConverter;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.repository.AvailabilityRepository;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AvailabilityHelper {
    private final AvailabilityConverter availabilityConverter;
    private final AuthService authService;
    private final AvailabilityRepository availabilityRepository;

    public AvailabilityHelper(AvailabilityConverter availabilityConverter, AuthService authService, AvailabilityRepository availabilityRepository) {
        this.availabilityConverter = availabilityConverter;
        this.authService = authService;
        this.availabilityRepository = availabilityRepository;
    }

    // create avail if none exist
    public Availability createAvailability(UpdateAvailabilityRequest request) {

        Availability availability = new Availability();

        List<Period> periods = request.getPeriods();
        List<String> ids = new ArrayList<>();
        for (Period period : periods) {
            ids.add(period.getId());
        }

        availability.setCaregiverId(request.getCaregiverId());
        availability.setPeriods(ids);

        Availability createdAvailability = availability;

        return createdAvailability;
    }

}
