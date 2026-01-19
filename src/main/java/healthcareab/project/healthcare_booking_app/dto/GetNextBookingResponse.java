package healthcareab.project.healthcare_booking_app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class GetNextBookingResponse {
    private String bookingId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String dayOfWeek;
    private String fullName;
    private List<String> symptoms;
    private String reason;
    private String note;

    public GetNextBookingResponse(String bookingId, LocalDateTime startDateTime, LocalDateTime endDateTime, String dayOfWeek, String fullName, List<String> symptoms, String reason, String note) {
        this.bookingId = bookingId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.dayOfWeek = dayOfWeek;
        this.fullName = fullName;
        this.symptoms = symptoms;
        this.reason = reason;
        this.note = note;
    }

    public String getBookingId() {
        return bookingId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public String getReason() {
        return reason;
    }

    public String getNote() {
        return note;
    }
}
