package healthcareab.project.healthcare_booking_app.converters;

import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingHistoryResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingsResponse;
import healthcareab.project.healthcare_booking_app.models.Booking;
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
}
