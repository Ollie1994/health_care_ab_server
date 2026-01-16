package healthcareab.project.healthcare_booking_app.dto;

public class UpdateAvailabilityResponse {
    private String message;



    public UpdateAvailabilityResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
