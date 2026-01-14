package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.controllers.BookingController;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.PatchBookingResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingHistoryResponse;
import healthcareab.project.healthcare_booking_app.dto.GetBookingsResponse;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import healthcareab.project.healthcare_booking_app.services.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    @Test
    void createBooking_shouldReturnCreated() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest();
        CreateBookingResponse response =
                new CreateBookingResponse("Booking created", "Adam", LocalDateTime.of(2026, 8, 5, 10, 30),
                        LocalDateTime.of(2026, 8, 5, 11, 0));

        when(bookingService.createBooking(request)).thenReturn(response);

        // Act
        ResponseEntity<CreateBookingResponse> result =
                bookingController.createBooking(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getMyBookings_shouldReturnListOfGetBookingsResponse() {
        // --- Arrange ---
        List<GetBookingsResponse> bookingsResponseList = List.of(
                new GetBookingsResponse(
                        LocalDateTime.of(2026, 8, 5, 10, 0),
                        LocalDateTime.of(2026, 8, 5, 11, 0),
                        BookingStatus.CONFIRMED,
                        "Dr McCaregiver",
                        List.of("Flu", "Cough"),
                        "BOOKING_ID_1"
                ),
                new GetBookingsResponse(
                        LocalDateTime.of(2026, 8, 6, 14, 0),
                        LocalDateTime.of(2026, 8, 6, 15, 30),
                        BookingStatus.CONFIRMED,
                        "Dr McCaregiver",
                        List.of("Headache", "Fatigue"),
                        "BOOKING_ID_2"
                )
        );

        when(bookingService.getMyBookings()).thenReturn(bookingsResponseList);

        // --- Act ---
        List<GetBookingsResponse> result = bookingController.getMyBookings();

        // --- Assert ---
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingsResponseList, result);
    }

    @Test
    void getMyBookingHistory_shouldReturnListOfGetBookingHistoryResponse() {
        // --- Arrange ---
        List<GetBookingHistoryResponse> historyResponseList = List.of(
                new GetBookingHistoryResponse(
                        LocalDateTime.of(2026, 8, 1, 9, 0),
                        "Dr McCaregiver",
                        "BOOKING_ID_1"
                ),
                new GetBookingHistoryResponse(
                        LocalDateTime.of(2026, 8, 2, 13, 30),
                        "Dr McCaregiver",
                        "BOOKING_ID_2"
                )
        );

        when(bookingService.getMyBookingHistory()).thenReturn(historyResponseList);

        // --- Act ---
        List<GetBookingHistoryResponse> result = bookingController.getMyBookingHistory();

        // --- Assert ---
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(historyResponseList, result);
    }


    @Test
    void createBooking_shouldThrowException_whenServiceFails() {
        // Arrange
        CreateBookingRequest request = new CreateBookingRequest();

        when(bookingService.createBooking(request))
                .thenThrow(new RuntimeException("Booking failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookingController.createBooking(request)
        );

        assertEquals("Booking failed", exception.getMessage());
    }

    // -------------------- CANCEL BOOKING TESTS --------------------
    @Test
    void cancelBooking_shouldReturnPatchBookingResponse_whenSuccessful() {
        // --- ARRANGE ---
        PatchBookingResponse response = new PatchBookingResponse(
                "booking1",
                "Adam",
                null, // status kan vara null för test, eller BookingStatus.CANCELLED
                LocalDateTime.of(2026, 8, 5, 10, 30),
                LocalDateTime.of(2026, 8, 5, 11, 0),
                "Booking has been cancelled"
        );

        when(bookingService.cancelBooking("booking1")).thenReturn(response);

        // --- ACT ---
        ResponseEntity<PatchBookingResponse> result = bookingController.cancelBooking("booking1");

        // --- ASSERT ---
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void cancelBooking_shouldThrowException_whenServiceFails() {
        // --- ARRANGE ---
        when(bookingService.cancelBooking("booking1")).thenThrow(new RuntimeException("Cancel failed"));

        // --- ACT & ASSERT ---
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookingController.cancelBooking("booking1")
        );

        assertEquals("Cancel failed", exception.getMessage());
    }
}

