package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.BookingStatus;

import java.time.LocalDateTime;

public class PatchBookingResponse {

    private String bookingId;
    private String caregiver_first_name;
    private BookingStatus status;
    private LocalDateTime start_date_time;
    private LocalDateTime end_date_time;
    private String message;

    public PatchBookingResponse(
            String bookingId,
            String caregiver_first_name,
            BookingStatus status,
            LocalDateTime start_date_time,
            LocalDateTime end_date_time,
            String message
    ) {
        this.bookingId = bookingId;
        this.caregiver_first_name = caregiver_first_name;
        this.status = status;
        this.start_date_time = start_date_time;
        this.end_date_time = end_date_time;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCaregiver_first_name() {
        return caregiver_first_name;
    }

    public void setCaregiver_first_name(String caregiver_first_name) {
        this.caregiver_first_name = caregiver_first_name;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
