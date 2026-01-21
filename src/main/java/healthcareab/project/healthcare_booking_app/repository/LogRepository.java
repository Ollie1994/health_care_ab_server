package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.Log;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<Log, String> {
}
