package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByPatient_id(String id);
    List<Booking> findByCaregiver_id(String id);
}