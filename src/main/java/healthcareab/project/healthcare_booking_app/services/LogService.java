package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.models.ActionPerformed;
import healthcareab.project.healthcare_booking_app.models.Log;
import healthcareab.project.healthcare_booking_app.repository.LogRepository;
import healthcareab.project.healthcare_booking_app.utils.UserIdSalter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(ActionPerformed actionPerformed, String userExposed, String performedBy, boolean success) {

        // Salt user id's before saving
        String saltedUserExposed = UserIdSalter.salt(userExposed);
        String saltedPerformedBy = UserIdSalter.salt(performedBy);

        Log log = new Log(
                null,
                LocalDateTime.now(),
                saltedUserExposed,
                saltedPerformedBy,
                success,
                actionPerformed
        );
        logRepository.save(log);
    }


}
