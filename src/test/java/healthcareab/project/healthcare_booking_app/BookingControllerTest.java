package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.controllers.BookingController;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingRequest;
import healthcareab.project.healthcare_booking_app.dto.CreateBookingResponse;
import healthcareab.project.healthcare_booking_app.services.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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


}
