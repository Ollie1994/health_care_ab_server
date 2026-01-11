package healthcareab.project.healthcare_booking_app.dto;

import java.time.LocalDateTime;

public class CreateBookingResponse {

    private String message;

    private String caregiver_first_name;

    private LocalDateTime start_date_time;

    private LocalDateTime end_date_time;

    public CreateBookingResponse() {
    }


    public CreateBookingResponse(String message, String caregiver_first_name, LocalDateTime start_date_time, LocalDateTime end_date_time) {
        this.message = message;
        this.caregiver_first_name = caregiver_first_name;
        this.start_date_time = start_date_time;
        this.end_date_time = end_date_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCaregiver_first_name() {
        return caregiver_first_name;
    }

    public void setCaregiver_first_name(String caregiver_first_name) {
        this.caregiver_first_name = caregiver_first_name;
    }

    public LocalDateTime getStart_date_time() {
        return start_date_time;
    }

    public void setStart_date_time(LocalDateTime start_date_time) {
        this.start_date_time = start_date_time;
    }

    public LocalDateTime getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(LocalDateTime end_date_time) {
        this.end_date_time = end_date_time;
    }
}
