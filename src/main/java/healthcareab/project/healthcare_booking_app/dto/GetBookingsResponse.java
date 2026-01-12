package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public class GetBookingsResponse {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BookingStatus status;
    private String fullName;
    private List<String> symptoms;
    private String bookingId;

    public GetBookingsResponse(LocalDateTime startDateTime, LocalDateTime endDateTime, BookingStatus status, String fullName, List<String> symptoms, String bookingId) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.fullName = fullName;
        this.symptoms = symptoms;
        this.bookingId = bookingId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public String getBookingId() {
        return bookingId;
    }
}
