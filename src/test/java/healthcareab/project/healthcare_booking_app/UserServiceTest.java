package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import healthcareab.project.healthcare_booking_app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User patient;

    @BeforeEach
    void setup() {
        patient = new User(
                "USER_ID",
                "patientUser",
                "password",
                Set.of(Role.PATIENT)
        );
        patient.setEmail("test@test.com");
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setSocialSecurityNumber("123456");
    }

    // ---------- ✅ POSITIVE ----------

    @Test
    void anonymizeUser_shouldAnonymizePatientUser() {
        // Arrange
        when(authService.getAuthenticated()).thenReturn(patient);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        // Act
        userService.anonymizeUser();

        // Assert
        assertTrue(patient.getIsAnonymous());
        assertNull(patient.getEmail());
        assertNull(patient.getFirstName());
        assertNull(patient.getLastName());
        assertNull(patient.getSocialSecurityNumber());
        assertTrue(patient.getUsername().startsWith("Anonymous_"));
        assertEquals("encodedPassword", patient.getPassword());
        assertTrue(patient.getRoles().isEmpty());

        verify(userRepository).save(patient);
    }

    // ---------- ❌ NEGATIVE ----------

    @Test
    void anonymizeUser_shouldThrowAccessDenied_whenNotPatient() {
        // Arrange
        User caregiver = new User(
                "CAREGIVER_ID",
                "caregiver",
                "password",
                Set.of(Role.CAREGIVER)
        );

        when(authService.getAuthenticated()).thenReturn(caregiver);

        // Act + Assert
        assertThrows(AccessDeniedException.class,
                () -> userService.anonymizeUser());

        verify(userRepository, never()).save(any());
    }
}
