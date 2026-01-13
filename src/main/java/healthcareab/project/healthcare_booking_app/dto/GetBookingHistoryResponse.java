package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public class GetBookingHistoryResponse {
    private LocalDateTime startDateTime;
    private String fullName;
    private String bookingId;

    public GetBookingHistoryResponse(LocalDateTime startDateTime, String fullName, String bookingId) {
        this.startDateTime = startDateTime;
        this.fullName = fullName;
        this.bookingId = bookingId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBookingId() {
        return bookingId;
    }
}
