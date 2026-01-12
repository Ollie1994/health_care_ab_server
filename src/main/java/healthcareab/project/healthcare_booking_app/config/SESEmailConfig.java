package healthcareab.project.healthcare_booking_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Configuration
public class SESEmailConfig {
    @Value("${aws.access.key}")
    private String AWS_ACCESS_KEY;
    @Value("${aws.secret.key}")
    private String AWS_SECRET_KEY;

    @Value("${aws.sender.email}")
    private String senderEmail;
    @Value("${aws.recipient.email}")
    private String recipientEmail;
    String subject = "Confirmation Email";
    String htmlMessage = "Your appointment is now booked";


    private StaticCredentialsProvider awsCredentials() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY)
        );
    }


    private SesClient emailClientBuilder() {
        return SesClient.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(awsCredentials()) // from previous rewrite
                .build();
    }


    public void sendSESEmail() {

        SesClient mailClient = emailClientBuilder();

        SendEmailRequest request = SendEmailRequest.builder()
                .source(senderEmail)
                .destination(Destination.builder().toAddresses(recipientEmail).build())
                .message(Message.builder().subject(Content.builder().charset("UTF-8").data(subject).build()).body(Body.builder().html(Content.builder().charset("UTF-8").data(htmlEmailTemplate(htmlMessage)).build()).build()).build())
                .build();

        mailClient.sendEmail(request);


    }

    private String htmlEmailTemplate(String message) {
        return message;
    }


}