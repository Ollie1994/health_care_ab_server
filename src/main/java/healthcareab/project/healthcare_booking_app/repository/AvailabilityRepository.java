package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AvailabilityRepository extends MongoRepository<Availability, String> {
    Optional<Availability> findByCaregiverId(String id);
}
