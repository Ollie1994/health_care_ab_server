package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.BookingStatus;

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

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
