package healthcareab.project.healthcare_booking_app.controllers;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.models.Period;
import healthcareab.project.healthcare_booking_app.services.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }


    @PatchMapping("/update")
    public ResponseEntity<UpdateAvailabilityResponse> updateAvailability(@Valid @RequestBody UpdateAvailabilityRequest request) {
        UpdateAvailabilityResponse response = availabilityService.updateAvailability(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @PatchMapping("/delete/{id}")
    public ResponseEntity<?> deleteAvailabilityPeriodById(@PathVariable String id) {
        availabilityService.deleteAvailabilityPeriodById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<GetAvailabilityResponse> GetAvailabilityById(@PathVariable String id) {
//        GetAvailabilityResponse response = availabilityService.getAvailabilityById(id);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @GetMapping
    public ResponseEntity<List<Period>> getMyAvailability() {
        List<Period> response = availabilityService.getMyAvailability();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
