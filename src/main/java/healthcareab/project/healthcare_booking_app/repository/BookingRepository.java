package healthcareab.project.healthcare_booking_app.repository;

import healthcareab.project.healthcare_booking_app.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByPatientIdOrderByStartDateTimeDesc(String id);
    List<Booking> findByCaregiverIdOrderByStartDateTimeDesc(String id);
    List<Booking> findByPatientIdAndEndDateTimeBeforeOrderByStartDateTimeDesc(String patientId, LocalDateTime endTime);
    List<Booking> findByCaregiverIdAndEndDateTimeBeforeOrderByStartDateTimeDesc(String patientId, LocalDateTime endTime);
    Booking findFirstByPatientIdAndStartDateTimeAfterOrderByStartDateTimeAsc(
            String patientId,
            LocalDateTime now
    );
    Booking findFirstByCaregiverIdAndStartDateTimeAfterOrderByStartDateTimeAsc(
            String caregiverId,
            LocalDateTime now
    );
}