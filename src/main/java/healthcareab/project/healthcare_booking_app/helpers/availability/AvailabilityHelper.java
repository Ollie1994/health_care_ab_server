package healthcareab.project.healthcare_booking_app.helpers.availability;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.models.Availability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AvailabilityHelper {

    public Availability createAvailability(UpdateAvailabilityRequest request) {

        Availability availability = new Availability();

        availability.setCaregiverId(request.getCaregiverId());
        availability.setPeriods(new ArrayList<>());

        Availability createdAvailability = availability;

        return createdAvailability;
    }

}
