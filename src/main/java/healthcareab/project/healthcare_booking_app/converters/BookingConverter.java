package healthcareab.project.healthcare_booking_app.converters;

import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.PatchBookingResponse;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.User;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter {




    public CreateBookingResponse convertToCreateBookingResponse(Booking booking, User user) {

        return new CreateBookingResponse(
                "Booking has been booked successfully",
                user.getFirstName(),
                booking.getStart_date_time(),
                booking.getEnd_date_time()
        );
    }

    public PatchBookingResponse convertToPatchBookingResponse(Booking booking, User user) {
        return new PatchBookingResponse(
                booking.getId(),
                user.getFirstName(),
                booking.getStatus(),
                booking.getStart_date_time(),
                booking.getEnd_date_time(),
                "Booking has been cancelled successfully"
        );
    }
}
