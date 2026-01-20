package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.controllers.AvailabilityController;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.services.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    @Mock
    AvailabilityService availabilityService;

    @InjectMocks
    AvailabilityController availabilityController;

    // POSITIVE TESTS
    @Test
    void updateAvailability_shouldReturn_Updated() {

        // Arrange
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest("Caregiver_id", LocalDateTime.of(2026, 1, 20, 8, 0),
                LocalDateTime.of(2026, 1, 20, 9, 0));
        UpdateAvailabilityResponse response = new UpdateAvailabilityResponse("Availability has been updated successfully");
        when(availabilityService.updateAvailability(request)).thenReturn(response);

        // Act
        ResponseEntity<UpdateAvailabilityResponse> result =
                availabilityController.updateAvailability(request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getMyAvailability_shouldReturn_MyAvailabilityPeriods() {

        // Arrange
        List<Period> myPeriodsList = List.of(
                new Period(
                        "CargiverId1",
                        LocalDateTime.of(2026, 2, 2, 8, 30),
                        LocalDateTime.of(2026, 2, 2, 9, 30),
                        LocalDate.of(2026, 1, 18),
                        LocalDate.of(2026, 1, 18)
                ),
                new Period(
                        "CargiverId2",
                        LocalDateTime.of(2026, 2, 2, 10, 30),
                        LocalDateTime.of(2026, 2, 2, 11, 30),
                        LocalDate.of(2026, 1, 18),
                        LocalDate.of(2026, 1, 18)
                )
        );
        when(availabilityService.getMyAvailability()).thenReturn(myPeriodsList);

        // Act
        ResponseEntity<List<Period>> result = availabilityController.getMyAvailability();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(myPeriodsList, result.getBody());
    }


    @Test
    void deleteAvailabilityPeriodById_shouldReturn_204NoContent() {

        // Arrange
        String periodId = "period123";
        doNothing().when(availabilityService).deleteAvailabilityPeriodById(periodId);

        // Act
        ResponseEntity<?> response =
                availabilityController.deleteAvailabilityPeriodById(periodId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(availabilityService).deleteAvailabilityPeriodById(periodId);
    }

}
