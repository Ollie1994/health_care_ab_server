package healthcareab.project.healthcare_booking_app.controllers;

import healthcareab.project.healthcare_booking_app.dto.*;
import healthcareab.project.healthcare_booking_app.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        CreateBookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<GetBookingsResponse> getMyBookings() {
        return bookingService.getMyBookings();
    }

    @GetMapping("/history")
    public List<GetBookingHistoryResponse> getMyBookingHistory() {
        return bookingService.getMyBookingHistory();
    }

    @GetMapping("/upcoming")
    public GetNextBookingResponse getNextBooking() {
        return bookingService.getNextBooking();
    }

    // For cancelling a booking
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<PatchBookingResponse> cancelBooking(@PathVariable String id) {
        PatchBookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
