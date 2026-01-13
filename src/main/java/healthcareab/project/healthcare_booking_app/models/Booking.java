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

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private List<String> symptoms;

    private String reasonForVisit;

    private String notesFromPatient;

    private String feedback;

    private String notesFromCaregiver;

    private String employeeOnlyNotes;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;


    public Booking() {
    }

    //With ID for testing purposes
    public Booking(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(String caregiverId) {
        this.caregiverId = caregiverId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
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

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getNotesFromPatient() {
        return notesFromPatient;
    }

    public void setNotesFromPatient(String notesFromPatient) {
        this.notesFromPatient = notesFromPatient;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getNotesFromCaregiver() {
        return notesFromCaregiver;
    }

    public void setNotesFromCaregiver(String notesFromCaregiver) {
        this.notesFromCaregiver = notesFromCaregiver;
    }

    public String getEmployeeOnlyNotes() {
        return employeeOnlyNotes;
    }

    public void setEmployeeOnlyNotes(String employeeOnlyNotes) {
        this.employeeOnlyNotes = employeeOnlyNotes;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
