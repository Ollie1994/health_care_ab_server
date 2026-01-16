package healthcareab.project.healthcare_booking_app.helpers.availability;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.Period;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AvailabilityHelper {

    public Availability createAvailability(UpdateAvailabilityRequest request) {

        Availability availability = new Availability();

        Period period = request.getNewPeriod();
        List<String> ids = new ArrayList<>();
        ids.add(period.getId());

        availability.setCaregiverId(request.getCaregiverId());
        availability.setPeriods(ids);

        Availability createdAvailability = availability;

        return createdAvailability;
    }

}
