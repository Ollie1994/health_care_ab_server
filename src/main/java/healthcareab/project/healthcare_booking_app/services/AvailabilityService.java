package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.AvailabilityConverter;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.helpers.availability.AvailabilityHelper;
import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final AuthService authService;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityConverter availabilityConverter;
    private final AvailabilityHelper availabilityHelper;


    public AvailabilityService(AuthService authService, AvailabilityRepository availabilityRepository, AvailabilityConverter availabilityConverter, AvailabilityHelper availabilityHelper) {
        this.authService = authService;
        this.availabilityRepository = availabilityRepository;
        this.availabilityConverter = availabilityConverter;
        this.availabilityHelper = availabilityHelper;
    }


    public UpdateAvailabilityResponse updateAvailabilityById(UpdateAvailabilityRequest request, String id) {

        User caregiver = authService.getAuthenticated();
        Availability availability = availabilityRepository.findById(id).orElse(availabilityHelper.createAvailability(request));


        // kolla att request.getCaregiverId och id och caregiver.getId är exakt samma
        // kolla att caregiver har rollen CAREGIVER

        // update periods
        // update availa
        availabilityRepository.save(availability);
        return availabilityConverter.convertToUpdateAvailabilityResponse(availability);
    }


}
