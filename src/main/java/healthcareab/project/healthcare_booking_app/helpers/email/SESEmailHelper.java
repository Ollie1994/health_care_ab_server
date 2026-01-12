package healthcareab.project.healthcare_booking_app.helpers.email;

import healthcareab.project.healthcare_booking_app.config.SESEmailConfig;
import org.springframework.stereotype.Service;

@Service
public class SESEmailHelper {

    private final SESEmailConfig awsConfig;

    public SESEmailHelper(SESEmailConfig awsConfig) {
        this.awsConfig = awsConfig;
    }


    public void sendEmail() {
        awsConfig.sendSESEmail();
    }
}