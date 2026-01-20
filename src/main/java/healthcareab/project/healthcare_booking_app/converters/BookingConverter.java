package healthcareab.project.healthcare_booking_app.converters;

import healthcareab.project.healthcare_booking_app.dto.*;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.User;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter {

    public CreateBookingResponse convertToCreateBookingResponse(Booking booking, User user) {

        return new CreateBookingResponse(
                "Booking has been booked successfully",
                user.getFirstName(),
                booking.getStartDateTime(),
                booking.getEndDateTime()
        );
    }

    public GetBookingsResponse convertToGetBookingsResponse (Booking booking, String fullName) {
        return new GetBookingsResponse(booking.getStartDateTime(), booking.getEndDateTime(), booking.getStatus(), fullName, booking.getSymptoms(), booking.getId());
    }

    public GetBookingHistoryResponse convertToGetBookingHistoryResponse (Booking booking, String fullName) {
        return new GetBookingHistoryResponse(booking.getStartDateTime(), fullName, booking.getId());
    }

    public GetNextBookingResponse convertToGetNextBookingResponse (Booking booking, String dayOfWeek, String fullName) {
        return new GetNextBookingResponse(booking.getId(), booking.getStartDateTime(), booking.getEndDateTime(), dayOfWeek, fullName, booking.getSymptoms(), booking.getReasonForVisit(), booking.getNotesFromPatient());
    }

    public PatchBookingResponse convertToPatchBookingResponse(Booking booking, User user) {
        return new PatchBookingResponse(
                booking.getId(),
                user.getFirstName(),
                booking.getStatus(),
                booking.getStartDateTime(),
                booking.getEndDateTime(),
                "Booking has been cancelled successfully"
        );
    }
}
