package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface BookingRepository extends MongoRepository<Booking, String> {
}