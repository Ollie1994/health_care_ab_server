package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.controllers.AuthController;
import healthcareab.project.healthcare_booking_app.dto.AuthRequest;
import healthcareab.project.healthcare_booking_app.dto.RegisterRequest;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import healthcareab.project.healthcare_booking_app.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {


    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /* =====================================================
       REGISTER
       ===================================================== */

    @Test
    void register_shouldCreateUser_whenUsernameDoesNotExist() {
        // ---------- Arrange ----------
        RegisterRequest request = new RegisterRequest(
                "john",
                "password",
                Set.of(Role.USER),
                "john@email.com",
                "John",
                "Doe"
        );

        when(authService.existsByUsername("john")).thenReturn(false);

        // ---------- Act ----------
        ResponseEntity<?> response = authController.register(request);

        // ---------- Assert ----------
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService).registerUser(any(User.class));
    }

    @Test
    void register_shouldReturnConflict_whenUsernameAlreadyExists() {
        // ---------- Arrange ----------
        RegisterRequest request = new RegisterRequest(
                "john",
                "password",
                Set.of(Role.USER),
                "john@email.com",
                "John",
                "Doe"
        );

        when(authService.existsByUsername("john")).thenReturn(true);

        // ---------- Act ----------
        ResponseEntity<?> response = authController.register(request);

        // ---------- Assert ----------
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists.", response.getBody());
        verify(authService, never()).registerUser(any());
    }

    /* =====================================================
       LOGIN
       ===================================================== */

    @Test
    void login_shouldAuthenticateAndReturnJwt_whenCredentialsAreValid() {
        // ---------- Arrange ----------
        AuthRequest request = new AuthRequest("john", "password");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "john",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        User user = new User();
        user.setUsername("john");
        user.setRoles(Set.of(Role.USER));

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtUtil.generateToken(userDetails))
                .thenReturn("jwt-token");

        when(authService.findByUsername("john"))
                .thenReturn(user);

        // ---------- Act ----------
        ResponseEntity<?> response = authController.login(request, httpServletResponse);

        // ---------- Assert ----------
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Set-Cookie"));
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() {
        // ---------- Arrange ----------
        AuthRequest request = new AuthRequest("john", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // ---------- Act ----------
        ResponseEntity<?> response = authController.login(request, httpServletResponse);

        // ---------- Assert ----------
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody());
    }

    /* =====================================================
       LOGOUT
       ===================================================== */

    @Test
    void logout_shouldClearSecurityContextAndExpireCookie() {
        // ---------- Arrange ----------
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        // ---------- Act ----------
        ResponseEntity<?> response = authController.logout(httpServletResponse);

        // ---------- Assert ----------
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(response.getHeaders().containsKey("Set-Cookie"));
    }

    /* =====================================================
       CHECK AUTHENTICATION
       ===================================================== */

    @Test
    void checkAuthentication_shouldReturnUnauthorized_whenNotAuthenticated() {
        // ---------- Arrange ----------
        SecurityContextHolder.clearContext();

        // ---------- Act ----------
        ResponseEntity<?> response = authController.checkAuthentication();

        // ---------- Assert ----------
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated!", response.getBody());
    }

    @Test
    void checkAuthentication_shouldReturnUserInfo_whenAuthenticated() {
        // ---------- Arrange ----------
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "john",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setUsername("john");
        user.setRoles(Set.of(Role.USER));

        when(authService.findByUsername("john")).thenReturn(user);

        // ---------- Act ----------
        ResponseEntity<?> response = authController.checkAuthentication();

        // ---------- Assert ----------
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }








}
