package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.converters.BookingConverter;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.PatchBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingHistoryResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingsResponse;
import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingConverterTest {

    private BookingConverter bookingConverter;

    @BeforeEach
    void setUp() {
        bookingConverter = new BookingConverter();
    }

    @Test
    void testConvertToCreateBookingResponse_Success() {
        // --- Arrange ---
        User user = new User();
        user.setFirstName("John");

        Booking booking = new Booking();
        booking.setStartDateTime(LocalDateTime.of(2026, 1, 12, 10, 0));
        booking.setEndDateTime(LocalDateTime.of(2026, 1, 12, 12, 0));

        // --- Act ---
        CreateBookingResponse response = bookingConverter.convertToCreateBookingResponse(booking, user);

        // --- Assert ---
        assertNotNull(response);
        assertEquals("Booking has been booked successfully", response.getMessage());
        assertEquals("John", response.getCaregiverFirstName());
        assertEquals(booking.getStartDateTime(), response.getStartDateTime());
        assertEquals(booking.getEndDateTime(), response.getEndDateTime());
    }

    @Test
    void testConvertToCreateBookingResponse_NullBooking_ThrowsException() {
        // --- Arrange ---
        User user = new User();
        user.setFirstName("John");

        Booking booking = null;

        // --- Act & Assert ---
        assertThrows(NullPointerException.class, () ->
                bookingConverter.convertToCreateBookingResponse(booking, user)
        );
    }

    @Test
    void testConvertToCreateBookingResponse_NullUser_ThrowsException() {
        // --- Arrange ---
        User user = null;

        Booking booking = new Booking();
        booking.setStartDateTime(LocalDateTime.of(2026, 1, 12, 10, 0));
        booking.setEndDateTime(LocalDateTime.of(2026, 1, 12, 12, 0));

        // --- Act & Assert ---
        assertThrows(NullPointerException.class, () ->
                bookingConverter.convertToCreateBookingResponse(booking, user)
        );
    }

    @Test
    void testConvertToCreateBookingResponse_NullFieldsInBookingOrUser() {
        // --- Arrange ---
        User user = new User(); // firstName is null
        Booking booking = new Booking(); // start and end are null

        // --- Act ---
        CreateBookingResponse response = bookingConverter.convertToCreateBookingResponse(booking, user);

        // --- Assert ---
        assertNotNull(response);
        assertNull(response.getCaregiverFirstName());
        assertNull(response.getStartDateTime());
        assertNull(response.getEndDateTime());
        assertEquals("Booking has been booked successfully", response.getMessage());
    }

    @Test
    void testConvertToGetBookingsResponse() {
        // --- Arrange ---
        Booking booking = new Booking("BOOKING_ID_1");
        booking.setStartDateTime(LocalDateTime.of(2026, 1, 12, 10, 0));
        booking.setEndDateTime(LocalDateTime.of(2026, 1, 12, 12, 0));
        booking.setStatus(BookingStatus.CONFIRMED);

        List<String> symptoms = new ArrayList<>();
        symptoms.add("Fever");
        symptoms.add("Cough");
        booking.setSymptoms(symptoms);

        // --- Act ---
        GetBookingsResponse response =
                bookingConverter.convertToGetBookingsResponse(booking, "John Doe");

        // --- Assert ---
        assertNotNull(response);
        assertEquals(booking.getStartDateTime(), response.getStartDateTime());
        assertEquals(booking.getEndDateTime(), response.getEndDateTime());
        assertEquals(booking.getStatus(), response.getStatus());
        assertEquals("John Doe", response.getFullName());
        assertEquals(symptoms, response.getSymptoms());
        assertEquals(booking.getId(), response.getBookingId());
    }

    @Test
    void testConvertToGetBookingHistoryResponse() {
        // --- Arrange ---
        Booking booking = new Booking("BOOKING_ID_1");
        booking.setStartDateTime(LocalDateTime.of(2026, 1, 12, 10, 0));

        // --- Act ---
        GetBookingHistoryResponse response =
                bookingConverter.convertToGetBookingHistoryResponse(booking, "John Doe");

        // --- Assert ---
        assertNotNull(response);
        assertEquals(booking.getStartDateTime(), response.getStartDateTime());
        assertEquals("John Doe", response.getFullName());
        assertEquals(booking.getId(), response.getBookingId());
    }

    // -------------------- PATCH BOOKING TESTS --------------------
    @Test
    void testConvertToPatchBookingResponse_Success() {
        // --- Arrange ---
        User user = new User();
        user.setFirstName("John");

        Booking booking = new Booking() {
            @Override
            public String getId() {
                return "booking1"; // dummy id för test
            }
        };
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setStartDateTime(LocalDateTime.of(2026, 1, 12, 10, 0));
        booking.setEndDateTime(LocalDateTime.of(2026, 1, 12, 12, 0));

        // --- Act ---
        PatchBookingResponse response = bookingConverter.convertToPatchBookingResponse(booking, user);

        // --- Assert ---
        assertNotNull(response);
        assertEquals("booking1", response.getBookingId());
        assertEquals("John", response.getCaregiver_first_name());
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        assertEquals(booking.getStartDateTime(), response.getStart_date_time());
        assertEquals(booking.getEndDateTime(), response.getEnd_date_time());
        assertEquals("Booking has been cancelled successfully", response.getMessage());
    }

    @Test
    void testConvertToPatchBookingResponse_NullBooking_ThrowsException() {
        User user = new User();
        user.setFirstName("John");
        Booking booking = null;

        assertThrows(NullPointerException.class, () ->
                bookingConverter.convertToPatchBookingResponse(booking, user)
        );
    }

    @Test
    void testConvertToPatchBookingResponse_NullUser_ThrowsException() {
        User user = null;

        Booking booking = new Booking() {
            @Override
            public String getId() {
                return "booking1";
            }
        };
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setStartDateTime(LocalDateTime.of(2026, 1, 12, 10, 0));
        booking.setEndDateTime(LocalDateTime.of(2026, 1, 12, 12, 0));

        assertThrows(NullPointerException.class, () ->
                bookingConverter.convertToPatchBookingResponse(booking, user)
        );
    }

    @Test
    void testConvertToPatchBookingResponse_NullFieldsInBookingOrUser() {
        User user = new User(); // firstName null
        Booking booking = new Booking(); // id, status, start/end null

        PatchBookingResponse response = bookingConverter.convertToPatchBookingResponse(booking, user);

        assertNotNull(response);
        assertNull(response.getBookingId());
        assertNull(response.getCaregiver_first_name());
        assertNull(response.getStatus());
        assertNull(response.getStart_date_time());
        assertNull(response.getEnd_date_time());
        assertEquals("Booking has been cancelled successfully", response.getMessage());
    }
}