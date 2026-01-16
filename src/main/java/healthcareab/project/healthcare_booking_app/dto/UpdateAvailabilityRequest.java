package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.Period;

public class UpdateAvailabilityRequest {

    private String caregiverId;

    private Period newPeriod;

    public UpdateAvailabilityRequest() {
    }

    public UpdateAvailabilityRequest(String caregiverId, Period newPeriod) {
        this.caregiverId = caregiverId;
        this.newPeriod = newPeriod;
    }


    public String getCaregiverId() {
        return caregiverId;
    }

    public Period getNewPeriod() {
        return newPeriod;
    }
}
