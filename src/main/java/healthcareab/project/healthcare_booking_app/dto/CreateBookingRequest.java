package healthcareab.project.healthcare_booking_app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateBookingRequest {

    private String caregiver_id;

    private LocalDateTime start_date_time;

    private LocalDateTime end_date_time;

    private List<String> symptoms;

    private String reason_for_visit;

    private String notes_from_patient;

    // maybe ???
    //private List<String> related_appointment_ids;


    public CreateBookingRequest() {
    }


    public CreateBookingRequest(String caregiver_id, LocalDateTime start_date_time, LocalDateTime end_date_time, List<String> symptoms, String reason_for_visit, String notes_from_patient) {
        this.caregiver_id = caregiver_id;
        this.start_date_time = start_date_time;
        this.end_date_time = end_date_time;
        this.symptoms = symptoms;
        this.reason_for_visit = reason_for_visit;
        this.notes_from_patient = notes_from_patient;
    }

    public String getCaregiver_id() {
        return caregiver_id;
    }

    public LocalDateTime getStart_date_time() {
        return start_date_time;
    }

    public LocalDateTime getEnd_date_time() {
        return end_date_time;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public String getReason_for_visit() {
        return reason_for_visit;
    }

    public String getNotes_from_patient() {
        return notes_from_patient;
    }
}
