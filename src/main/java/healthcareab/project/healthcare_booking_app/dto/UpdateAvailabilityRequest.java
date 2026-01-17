package healthcareab.project.healthcare_booking_app.dto;

import java.time.LocalDateTime;

public class UpdateAvailabilityRequest {

    private String caregiverId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public UpdateAvailabilityRequest() {
    }

    public UpdateAvailabilityRequest(String caregiverId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.caregiverId = caregiverId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }


    public String getCaregiverId() {
        return caregiverId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
}
