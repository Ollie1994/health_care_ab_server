package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.filters.JwtAuthenticationFilter;
import healthcareab.project.healthcare_booking_app.services.CustomUserDetailsService;
import healthcareab.project.healthcare_booking_app.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /* =====================================================
       POSITIVE: VALID JWT IN AUTH HEADER
       ===================================================== */

    @Test
    void doFilterInternal_shouldAuthenticateUser_whenJwtIsValidInHeader() throws Exception {
        // ---------- Arrange ----------
        String jwt = "valid-jwt";
        String username = "john";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, "password", List.of()
        );

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtUtil.extractUsername(jwt))
                .thenReturn(username);

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(jwt, userDetails))
                .thenReturn(true);

        // ---------- Act ----------
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // ---------- Assert ----------
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        verify(filterChain).doFilter(request, response);
    }

    /* =====================================================
       POSITIVE: VALID JWT IN COOKIE
       ===================================================== */

    @Test
    void doFilterInternal_shouldAuthenticateUser_whenJwtIsInCookie() throws Exception {
        // ---------- Arrange ----------
        String jwt = "valid-jwt";
        String username = "john";

        Cookie jwtCookie = new Cookie("jwt", jwt);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, "password", List.of()
        );

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        when(request.getCookies())
                .thenReturn(new Cookie[]{jwtCookie});

        when(jwtUtil.extractUsername(jwt))
                .thenReturn(username);

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(jwt, userDetails))
                .thenReturn(true);

        // ---------- Act ----------
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // ---------- Assert ----------
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    /* =====================================================
       NEGATIVE: INVALID JWT
       ===================================================== */

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenJwtIsInvalid() throws Exception {
        // ---------- Arrange ----------
        String jwt = "invalid-jwt";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtUtil.extractUsername(jwt))
                .thenThrow(new JwtException("Invalid token"));

        // ---------- Act ----------
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // ---------- Assert ----------
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /* =====================================================
       NEGATIVE: NO JWT PRESENT
       ===================================================== */

    @Test
    void doFilterInternal_shouldContinueFilterChain_whenNoJwtPresent() throws Exception {
        // ---------- Arrange ----------
        when(request.getHeader("Authorization"))
                .thenReturn(null);

        when(request.getCookies())
                .thenReturn(null);

        // ---------- Act ----------
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // ---------- Assert ----------
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}