package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "my-super-secret-key-for-testing-purposes-123456"; // ≥32 bytes for HS256
    private final int normalExpirationMs = 10_000; // 10 seconds for normal tests

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // manually set fields (since @Value injection won't work in unit test)
        setField(jwtUtil, "jwtSecret", secret);
        setField(jwtUtil, "jwtExpirationMs", normalExpirationMs);

        userDetails = new User("john", "password", Collections.emptyList());
    }

    // Helper to set private fields
    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = JwtUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /* =====================================================
       POSITIVE: GENERATE & VALIDATE TOKEN
    ===================================================== */
    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("john", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    /* =====================================================
       NEGATIVE: INVALID TOKEN
    ===================================================== */
    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        String invalidToken = "this-is-not-a-token";

        assertFalse(jwtUtil.validateToken(invalidToken, userDetails));
        assertThrows(JwtException.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    /* =====================================================
       NEGATIVE: EXPIRED TOKEN
    ===================================================== */
    @Test
    void validateToken_shouldReturnFalse_whenTokenIsExpired() throws Exception {
        // temporarily set expiration to -1 ms so token is already expired
        setField(jwtUtil, "jwtExpirationMs", -1);

        String token = jwtUtil.generateToken(userDetails);

        // ---------- Act & Assert ----------
        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    /* =====================================================
       NEGATIVE: WRONG USER
    ===================================================== */
    @Test
    void validateToken_shouldReturnFalse_whenUsernameDoesNotMatch() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = new User("alice", "password", Collections.emptyList());

        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    /* =====================================================
       POSITIVE: EXTRACT USERNAME
    ===================================================== */
    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken(userDetails);

        String username = jwtUtil.extractUsername(token);

        assertEquals("john", username);
    }
}