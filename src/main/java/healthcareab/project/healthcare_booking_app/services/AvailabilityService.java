package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.converters.AvailabilityConverter;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityRequest;
import healthcareab.project.healthcare_booking_app.dto.UpdateAvailabilityResponse;
import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.exceptions.ConflictException;
import healthcareab.project.healthcare_booking_app.helpers.availability.AvailabilityHelper;
import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.Role;
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

        if (!caregiver.getId().equals(request.getCaregiverId()) || !request.getCaregiverId().equals(id) || !id.equals(caregiver.getId())) {
            throw new ConflictException("Conflicting ids");
        }
        if (!caregiver.getRoles().contains(Role.CAREGIVER)) {
            throw new AccessDeniedException("Access denied");
        }

        Availability availability = availabilityRepository.findById(id).orElse(availabilityHelper.createAvailability(request));


        // kolla att caregiver har rollen CAREGIVER

        // update periods
        // update availa

        Availability updatedAvailability = availabilityRepository.save(availability);
        return availabilityConverter.convertToUpdateAvailabilityResponse(updatedAvailability);
    }


}
