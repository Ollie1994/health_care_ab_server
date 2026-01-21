package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.models.ActionPerformed;
import healthcareab.project.healthcare_booking_app.models.Log;
import healthcareab.project.healthcare_booking_app.repository.LogRepository;
import healthcareab.project.healthcare_booking_app.services.LogService;
import healthcareab.project.healthcare_booking_app.utils.UserIdSalter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogService logService;

    private String userExposed;
    private String performedBy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // --- ARRANGE COMMON DATA ---
        userExposed = "USER12345";
        performedBy = "ADMIN98765";
    }

    // POSITIVE SCENARIOS
    @Test
    void logShouldSaltUserIdsAndSaveLog() {
        // --- ARRANGE ---
        boolean success = true;
        ActionPerformed action = ActionPerformed.CREATED_ACCOUNT;

        // --- ACT ---
        logService.log(action, userExposed, performedBy, success);

        // --- ASSERT ---
        ArgumentCaptor<Log> logCaptor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository, times(1)).save(logCaptor.capture());

        Log savedLog = logCaptor.getValue();
        assertNotNull(savedLog.getTimeStamp(), "Timestamp should not be null");
        assertEquals(UserIdSalter.salt(userExposed), savedLog.getUserExposed());
        assertEquals(UserIdSalter.salt(performedBy), savedLog.getPerformedBy());
        assertEquals(success, savedLog.getSuccess());
        assertEquals(action, savedLog.getActionPerformed());
    }

    @Test
    void logShouldHandleNullUserIds() {
        // --- ARRANGE ---
        boolean success = false;
        ActionPerformed action = ActionPerformed.CREATED_ACCOUNT;

        // --- ACT ---
        logService.log(action, null, null, success);

        // --- ASSERT ---
        ArgumentCaptor<Log> logCaptor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository, times(1)).save(logCaptor.capture());

        Log savedLog = logCaptor.getValue();
        assertNull(savedLog.getUserExposed());
        assertNull(savedLog.getPerformedBy());
        assertEquals(success, savedLog.getSuccess());
        assertEquals(action, savedLog.getActionPerformed());
    }

    @Test
    void logShouldSaltOnlyUserExposedWhenPerformedByIsNull() {
        // --- ARRANGE ---
        boolean success = true;
        ActionPerformed action = ActionPerformed.CREATED_ACCOUNT;

        // --- ACT ---
        logService.log(action, userExposed, null, success);

        // --- ASSERT ---
        ArgumentCaptor<Log> logCaptor = ArgumentCaptor.forClass(Log.class);
        verify(logRepository, times(1)).save(logCaptor.capture());

        Log savedLog = logCaptor.getValue();
        assertEquals(UserIdSalter.salt(userExposed), savedLog.getUserExposed());
        assertNull(savedLog.getPerformedBy());
        assertEquals(success, savedLog.getSuccess());
        assertEquals(action, savedLog.getActionPerformed());
    }
}
