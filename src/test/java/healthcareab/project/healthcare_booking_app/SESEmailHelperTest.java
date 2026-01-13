package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.config.SESEmailConfig;
import healthcareab.project.healthcare_booking_app.helpers.email.SESEmailHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@ExtendWith(MockitoExtension.class)
class SESEmailHelperTest {

    @Mock
    private SESEmailConfig sesEmailConfig;

    @InjectMocks
    private SESEmailHelper sesEmailHelper;

    @Test
    void sendEmail_shouldDelegateToSESEmailConfig() {
        // --- ARRANGE ---
        String message = "Test message";
        String subject = "Test subject";
        String recipient = "test@example.com";

        // --- ACT ---
        sesEmailHelper.sendEmail(message, subject, recipient);

        // --- ASSERT ---
        verify(sesEmailConfig)
                .sendSESEmail(message, subject, recipient);

        verifyNoMoreInteractions(sesEmailConfig);
    }
}