package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository  extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
