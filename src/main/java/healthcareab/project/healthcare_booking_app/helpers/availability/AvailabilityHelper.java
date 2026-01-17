package healthcareab.project.healthcare_booking_app.helpers.availability;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.models.Availability;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityHelper {

    public Availability createAvailability(UpdateAvailabilityRequest request) {

        Availability availability = new Availability();

        // Period period = request.getNewPeriod();
        // List<String> ids = new ArrayList<>();
        // ids.add(period.getId());

        availability.setCaregiverId(request.getCaregiverId());
        //availability.setPeriods(ids);

        Availability createdAvailability = availability;
        System.out.println("CaregiverId - " + request.getCaregiverId());
        System.out.println("NewPeriodId - " + request.getNewPeriod().getId());
        System.out.println("NewPeriod Period - " + request.getNewPeriod().getStartDateTime() + " - " + request.getNewPeriod().getEndDateTime());

        return createdAvailability;
    }

}
