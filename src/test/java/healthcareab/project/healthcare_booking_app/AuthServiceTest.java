package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    //Mocka repository
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testFindByUsername_UserFound() {

        // --- Arrange ---
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // --- Act ---
        User result = authService.findByUsername(username);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(username, result.getUsername());

        // Verify repository interaction
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testFindByUsername_UserNotFound() {
        // --- Arrange ---
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.findByUsername(username);
        });

        // Verify repository interaction
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testExistsByUsername_WhenUserExists() {
        // --- Arrange ---
        String username = "existingUser";
        User mockUser = new User();
        mockUser.setUsername(username);

        // Mock repository to return user
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // --- Act ---
        boolean exists = authService.existsByUsername(username);

        // --- Assert ---
        assertTrue(exists);

        // Verify repository interaction
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testExistsByUsername_WhenUserDoesNotExist() {
        // --- Arrange ---
        String username = "newUser";

        // Mock repository to return empty
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // --- Act ---
        boolean exists = authService.existsByUsername(username);

        // --- Assert ---
        assertFalse(exists);

        // Verify repository interaction
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testRegisterUser_Success() {
        // --- Arrange ---
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("plainPassword");
        newUser.setRoles(null); // simulate missing roles

        // Mock passwordEncoder
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // --- Act ---
        authService.registerUser(newUser);

        // --- Assert ---
        assertEquals("encodedPassword", newUser.getPassword());
        assertNotNull(newUser.getRoles());
        assertTrue(newUser.getRoles().contains(Role.USER));

        // Verify that repository.save() was called exactly once with newUser
        verify(userRepository, times(1)).save(newUser);
        verifyNoMoreInteractions(userRepository);
        verify(passwordEncoder, times(1)).encode("plainPassword");
    }








}
