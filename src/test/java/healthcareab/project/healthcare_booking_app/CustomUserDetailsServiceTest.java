package healthcareab.project.healthcare_booking_app;


import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import healthcareab.project.healthcare_booking_app.services.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    /* =====================================================
       POSITIVE
       ===================================================== */

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // ---------- Arrange ----------
        User user = new User();
        user.setUsername("john");
        user.setPassword("password");
        user.setRoles(Set.of(Role.PATIENT, Role.CAREGIVER));

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        // ---------- Act ----------
        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("john");

        // ---------- Assert ----------
        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities =
                userDetails.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(
                authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))
        );
        assertTrue(
                authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CAREGIVER"))
        );

        verify(userRepository).findByUsername("john");
    }

    /* =====================================================
       NEGATIVE
       ===================================================== */

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        // ---------- Arrange ----------
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // ---------- Act & Assert ----------
        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername("unknown")
                );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername("unknown");
    }
}
