package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.Period;

import java.util.List;

public class UpdateAvailabilityRequest {

    private String caregiverId;

    private List<Period> periods;

    public UpdateAvailabilityRequest() {
    }

    public UpdateAvailabilityRequest(String caregiverId, List<Period> periods) {
        this.caregiverId = caregiverId;
        this.periods = periods;
    }


    public String getCaregiverId() {
        return caregiverId;
    }

    public List<Period> getPeriods() {
        return periods;
    }
}
