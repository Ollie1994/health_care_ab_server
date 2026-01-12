package healthcareab.project.healthcare_booking_app.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String patientId;

    private String caregiverId;

    private BookingStatus status;

    private LocalDateTime start_date_time;

    private LocalDateTime end_date_time;

    private List<String> symptoms;

    private String reason_for_visit;

    private String notes_from_patient;

    private String feedback;

    private String notes_from_caregiver;

    private String employee_only_notes;

    private List<String> related_appointment_ids;

    @CreatedDate
    private LocalDate created_at;

    @LastModifiedDate
    private LocalDate updated_at;


    public Booking() {
    }


    public String getId() {
        return id;
    }

    public String getPatient_id() {
        return patientId;
    }

    public void setPatient_id(String patient_id) {
        this.patientId = patient_id;
    }

    public String getCaregiver_id() {
        return caregiverId;
    }

    public void setCaregiver_id(String caregiver_id) {
        this.caregiverId = caregiver_id;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
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

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getReason_for_visit() {
        return reason_for_visit;
    }

    public void setReason_for_visit(String reason_for_visit) {
        this.reason_for_visit = reason_for_visit;
    }

    public String getNotes_from_patient() {
        return notes_from_patient;
    }

    public void setNotes_from_patient(String notes_from_patient) {
        this.notes_from_patient = notes_from_patient;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getNotes_from_caregiver() {
        return notes_from_caregiver;
    }

    public void setNotes_from_caregiver(String notes_from_caregiver) {
        this.notes_from_caregiver = notes_from_caregiver;
    }

    public String getEmployee_only_notes() {
        return employee_only_notes;
    }

    public void setEmployee_only_notes(String employee_only_notes) {
        this.employee_only_notes = employee_only_notes;
    }

    public List<String> getRelated_appointment_ids() {
        return related_appointment_ids;
    }

    public void setRelated_appointment_ids(List<String> related_appointment_ids) {
        this.related_appointment_ids = related_appointment_ids;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }
}
