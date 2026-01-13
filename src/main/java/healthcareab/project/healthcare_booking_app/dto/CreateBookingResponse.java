package healthcareab.project.healthcare_booking_app.dto;

import java.time.LocalDateTime;

public class CreateBookingResponse {

    private String message;

    private String caregiverFirstName;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public CreateBookingResponse() {
    }

    public CreateBookingResponse(String message, String caregiverFirstName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.message = message;
        this.caregiverFirstName = caregiverFirstName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCaregiverFirstName() {
        return caregiverFirstName;
    }

    public void setCaregiverFirstName(String caregiverFirstName) {
        this.caregiverFirstName = caregiverFirstName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
