package healthcareab.project.healthcare_booking_app.controllers;

import healthcareab.project.healthcare_booking_app.dto.*;
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

    @PostMapping
    public ResponseEntity<CreateAvailabilityResponse> createAvailability(@Valid @RequestBody CreateAvailabilityRequest request) {
        CreateAvailabilityResponse response = availabilityService.createAvailability(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateAvailabilityResponse> updateAvailabilityById(@PathVariable String id, @Valid @RequestBody UpdateAvailabilityRequest request) {
        UpdateAvailabilityResponse response = availabilityService.updateAvailabilityById(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetAvailabilityResponse> GetAvailabilityById(@PathVariable String id) {
        GetAvailabilityResponse response = availabilityService.getAvailabilityById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public List<GetMyAvailabilityResponse> getMyAvailability() {
        return availabilityService.getMyAvailability();
    }
}
