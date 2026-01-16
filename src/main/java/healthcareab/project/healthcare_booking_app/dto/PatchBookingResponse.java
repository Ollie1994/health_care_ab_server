package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.BookingStatus;

import java.time.LocalDateTime;

public class PatchBookingResponse {

    private String bookingId;
    private String caregiverFirstName;
    private BookingStatus status;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String message;

    public PatchBookingResponse(String bookingId, String caregiverFirstName, BookingStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime, String message) {
        this.bookingId = bookingId;
        this.caregiverFirstName = caregiverFirstName;
        this.status = status;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCaregiverFirstName() {
        return caregiverFirstName;
    }

    public void setCaregiverFirstName(String caregiverFirstName) {
        this.caregiverFirstName = caregiverFirstName;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
