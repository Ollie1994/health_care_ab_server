package healthcareab.project.healthcare_booking_app.converters;

import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
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
}
