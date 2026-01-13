package healthcareab.project.healthcare_booking_app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateBookingRequest {

    private String caregiverId;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private List<String> symptoms;

    private String reasonForVisit;

    private String notesFromPatient;


    public CreateBookingRequest() {
    }

    public CreateBookingRequest(String caregiverId, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> symptoms, String reasonForVisit, String notesFromPatient) {
        this.caregiverId = caregiverId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.symptoms = symptoms;
        this.reasonForVisit = reasonForVisit;
        this.notesFromPatient = notesFromPatient;
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

    public List<String> getSymptoms() {
        return symptoms;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public String getNotesFromPatient() {
        return notesFromPatient;
    }
}
