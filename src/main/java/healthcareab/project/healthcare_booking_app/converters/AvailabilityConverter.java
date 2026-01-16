package healthcareab.project.healthcare_booking_app.converters;

import healthcareab.project.healthcare_booking_app.dto.CreateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.models.Availability;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityConverter {


    public CreateAvailabilityResponse convertToCreateAvailabilityResponse(Availability availability) {
        return new CreateAvailabilityResponse(
        );
    }


    public UpdateAvailabilityResponse convertToUpdateAvailabilityResponse(Availability availability) {
        return new UpdateAvailabilityResponse(
        );
    }




}
