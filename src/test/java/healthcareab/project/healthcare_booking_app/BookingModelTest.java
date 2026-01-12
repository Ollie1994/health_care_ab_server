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
        booking.setPatientId("patient123");
        booking.setCaregiverId("caregiver456");
        booking.setStatus(BookingStatus.PENDING);
        booking.setStartDateTime(start);
        booking.setEndDateTime(end);
        booking.setSymptoms(List.of("fever", "cough"));
        booking.setReasonForVisit("Regular checkup");
        booking.setNotesFromPatient("Feeling tired");
        booking.setFeedback("Good service");
        booking.setNotesFromCaregiver("Patient advised rest");
        booking.setEmployeeOnlyNotes("Internal notes");
        booking.setRelatedAppointmentIds(List.of("apt1", "apt2"));
        booking.setCreated_at(created);
        booking.setUpdated_at(updated);

        // Assert
        assertEquals("patient123", booking.getPatientId());
        assertEquals("caregiver456", booking.getCaregiverId());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertEquals(start, booking.getStartDateTime());
        assertEquals(end, booking.getEndDateTime());
        assertEquals(List.of("fever", "cough"), booking.getSymptoms());
        assertEquals("Regular checkup", booking.getReasonForVisit());
        assertEquals("Feeling tired", booking.getNotesFromPatient());
        assertEquals("Good service", booking.getFeedback());
        assertEquals("Patient advised rest", booking.getNotesFromCaregiver());
        assertEquals("Internal notes", booking.getEmployeeOnlyNotes());
        assertEquals(List.of("apt1", "apt2"), booking.getRelatedAppointmentIds());
        assertEquals(created, booking.getCreated_at());
        assertEquals(updated, booking.getUpdated_at());
    }

    /* =====================================================
       NEGATIVE TESTS: check behavior with nulls or invalid inputs
       ===================================================== */
    @Test
    void booking_shouldHandleNullFieldsGracefully() {
        // Arrange
        booking.setPatientId(null);
        booking.setCaregiverId(null);
        booking.setStatus(null);
        booking.setStartDateTime(null);
        booking.setEndDateTime(null);
        booking.setSymptoms(null);
        booking.setReasonForVisit(null);
        booking.setNotesFromPatient(null);
        booking.setFeedback(null);
        booking.setNotesFromCaregiver(null);
        booking.setEmployeeOnlyNotes(null);
        booking.setRelatedAppointmentIds(null);
        booking.setCreated_at(null);
        booking.setUpdated_at(null);

        // Act & Assert
        assertNull(booking.getPatientId());
        assertNull(booking.getCaregiverId());
        assertNull(booking.getStatus());
        assertNull(booking.getStartDateTime());
        assertNull(booking.getEndDateTime());
        assertNull(booking.getSymptoms());
        assertNull(booking.getReasonForVisit());
        assertNull(booking.getNotesFromPatient());
        assertNull(booking.getFeedback());
        assertNull(booking.getNotesFromCaregiver());
        assertNull(booking.getEmployeeOnlyNotes());
        assertNull(booking.getRelatedAppointmentIds());
        assertNull(booking.getCreated_at());
        assertNull(booking.getUpdated_at());
    }

    @Test
    void booking_shouldAllowEmptyLists() {
        // Arrange
        booking.setSymptoms(List.of());
        booking.setRelatedAppointmentIds(List.of());

        // Act & Assert
        assertTrue(booking.getSymptoms().isEmpty());
        assertTrue(booking.getRelatedAppointmentIds().isEmpty());
    }

    @Test
    void booking_shouldDetectStartAfterEnd_asInvalidScenario() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2026, 8, 5, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 5, 11, 0);

        // Act
        booking.setStartDateTime(start);
        booking.setEndDateTime(end);

        // Assert (manual logic since model has no validation)
        assertTrue(booking.getStartDateTime().isAfter(booking.getEndDateTime()));
    }
}