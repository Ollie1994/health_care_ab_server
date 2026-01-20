package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.Period;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PeriodRepository extends MongoRepository<Period, String> {
}
