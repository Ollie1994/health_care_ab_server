package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.controllers.UserController;
import healthcareab.project.healthcare_booking_app.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void anonymizeUser_shouldAnonymizeUserAndClearJwtCookie() {
        // Arrange
        doNothing().when(userService).anonymizeUser();

        // Act
        ResponseEntity<?> response = userController.anonymizeUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User anonymized and logged out!", response.getBody());

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("jwt="));
        assertTrue(setCookieHeader.contains("Max-Age=0"));

        verify(userService, times(1)).anonymizeUser();
    }
}