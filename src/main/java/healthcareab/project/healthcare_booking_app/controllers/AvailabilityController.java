package healthcareab.project.healthcare_booking_app.controllers;

import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.services.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UpdateAvailabilityResponse> updateAvailabilityById(@Valid @RequestBody UpdateAvailabilityRequest request) {
        UpdateAvailabilityResponse response = availabilityService.updateAvailabilityById(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<GetAvailabilityResponse> GetAvailabilityById(@PathVariable String id) {
//        GetAvailabilityResponse response = availabilityService.getAvailabilityById(id);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

//    @GetMapping
//    public List<GetMyAvailabilityResponse> getMyAvailability() {
//        return availabilityService.getMyAvailability();
//    }

}
