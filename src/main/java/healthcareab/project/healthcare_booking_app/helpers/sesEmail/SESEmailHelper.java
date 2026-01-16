package healthcareab.project.healthcare_booking_app.helpers.sesEmail;

import healthcareab.project.healthcare_booking_app.config.SESEmailConfig;
import org.springframework.stereotype.Component;

@Component
public class SESEmailHelper {

    private final SESEmailConfig awsConfig;

    public SESEmailHelper(SESEmailConfig awsConfig) {
        this.awsConfig = awsConfig;
    }


    public void sendEmail(String htmlMessage, String subject, String recipientEmail) {
        awsConfig.sendSESEmail(htmlMessage, subject, recipientEmail);
    }
}