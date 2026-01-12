package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.models.Booking;
import healthcareab.project.healthcare_booking_app.models.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingModelTest {

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
    }

    /* =====================================================
       POSITIVE TESTS: verify getters and setters
       ===================================================== */
    @Test
    void booking_shouldStoreAndReturnAllFieldsCorrectly() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2026, 8, 5, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 5, 11, 0);
        LocalDate created = LocalDate.of(2026, 8, 1);
        LocalDate updated = LocalDate.of(2026, 8, 2);

        // Act
        booking.setPatient_id("patient123");
        booking.setCaregiver_id("caregiver456");
        booking.setStatus(BookingStatus.PENDING);
        booking.setStart_date_time(start);
        booking.setEnd_date_time(end);
        booking.setSymptoms(List.of("fever", "cough"));
        booking.setReason_for_visit("Regular checkup");
        booking.setNotes_from_patient("Feeling tired");
        booking.setFeedback("Good service");
        booking.setNotes_from_caregiver("Patient advised rest");
        booking.setEmployee_only_notes("Internal notes");
        booking.setRelated_appointment_ids(List.of("apt1", "apt2"));
        booking.setCreated_at(created);
        booking.setUpdated_at(updated);

        // Assert
        assertEquals("patient123", booking.getPatient_id());
        assertEquals("caregiver456", booking.getCaregiver_id());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertEquals(start, booking.getStart_date_time());
        assertEquals(end, booking.getEnd_date_time());
        assertEquals(List.of("fever", "cough"), booking.getSymptoms());
        assertEquals("Regular checkup", booking.getReason_for_visit());
        assertEquals("Feeling tired", booking.getNotes_from_patient());
        assertEquals("Good service", booking.getFeedback());
        assertEquals("Patient advised rest", booking.getNotes_from_caregiver());
        assertEquals("Internal notes", booking.getEmployee_only_notes());
        assertEquals(List.of("apt1", "apt2"), booking.getRelated_appointment_ids());
        assertEquals(created, booking.getCreated_at());
        assertEquals(updated, booking.getUpdated_at());
    }

    /* =====================================================
       NEGATIVE TESTS: check behavior with nulls or invalid inputs
       ===================================================== */
    @Test
    void booking_shouldHandleNullFieldsGracefully() {
        // Arrange
        booking.setPatient_id(null);
        booking.setCaregiver_id(null);
        booking.setStatus(null);
        booking.setStart_date_time(null);
        booking.setEnd_date_time(null);
        booking.setSymptoms(null);
        booking.setReason_for_visit(null);
        booking.setNotes_from_patient(null);
        booking.setFeedback(null);
        booking.setNotes_from_caregiver(null);
        booking.setEmployee_only_notes(null);
        booking.setRelated_appointment_ids(null);
        booking.setCreated_at(null);
        booking.setUpdated_at(null);

        // Act & Assert
        assertNull(booking.getPatient_id());
        assertNull(booking.getCaregiver_id());
        assertNull(booking.getStatus());
        assertNull(booking.getStart_date_time());
        assertNull(booking.getEnd_date_time());
        assertNull(booking.getSymptoms());
        assertNull(booking.getReason_for_visit());
        assertNull(booking.getNotes_from_patient());
        assertNull(booking.getFeedback());
        assertNull(booking.getNotes_from_caregiver());
        assertNull(booking.getEmployee_only_notes());
        assertNull(booking.getRelated_appointment_ids());
        assertNull(booking.getCreated_at());
        assertNull(booking.getUpdated_at());
    }

    @Test
    void booking_shouldAllowEmptyLists() {
        // Arrange
        booking.setSymptoms(List.of());
        booking.setRelated_appointment_ids(List.of());

        // Act & Assert
        assertTrue(booking.getSymptoms().isEmpty());
        assertTrue(booking.getRelated_appointment_ids().isEmpty());
    }

    @Test
    void booking_shouldDetectStartAfterEnd_asInvalidScenario() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2026, 8, 5, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 5, 11, 0);

        // Act
        booking.setStart_date_time(start);
        booking.setEnd_date_time(end);

        // Assert (manual logic since model has no validation)
        assertTrue(booking.getStart_date_time().isAfter(booking.getEnd_date_time()));
    }
}