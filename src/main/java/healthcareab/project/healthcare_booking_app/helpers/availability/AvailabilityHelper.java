package healthcareab.project.healthcare_booking_app.helpers.availability;

import healthcareab.project.healthcare_booking_app.models.Availability;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.AvailabilityRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AvailabilityHelper {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityHelper(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }


    public Availability createAvailability(User user) {

        Availability availability = new Availability();
        availability.setCaregiverId(user.getId());
        availability.setPeriods(new ArrayList<>());

        Availability savedAvailability = availabilityRepository.save(availability);

        return savedAvailability;
    }

    public Availability updateAvailability(Availability availability, List<String> periodIds, User user) {

        availability.setCaregiverId(user.getId());
        availability.setPeriods(periodIds);

        Availability savedAvailability = availabilityRepository.save(availability);

        return savedAvailability;
    }

}
