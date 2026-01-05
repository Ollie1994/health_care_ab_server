package healthcareab.project.healthcare_booking_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class HealthcareBookingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthcareBookingAppApplication.class, args);
	}

}
