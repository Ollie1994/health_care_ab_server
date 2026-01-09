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

    private final String secret = "my-super-secret-key-for-testing-purposes-123456"; // must be >= 32 bytes for HS256
    private final int expirationMs = 1000; // 1 second for test purposes

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // manually set fields (since @Value injection won't work in unit test)
        java.lang.reflect.Field secretField = JwtUtil.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, secret);

        java.lang.reflect.Field expirationField = JwtUtil.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, expirationMs);

        userDetails = new User("john", "password", Collections.emptyList());
    }

    /* =====================================================
       POSITIVE: GENERATE & VALIDATE TOKEN
       ===================================================== */
    @Test
    void generateToken_shouldReturnValidToken() {
        // ---------- Arrange ----------
        // done in @BeforeEach

        // ---------- Act ----------
        String token = jwtUtil.generateToken(userDetails);

        // ---------- Assert ----------
        assertNotNull(token);
        assertEquals("john", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    /* =====================================================
       NEGATIVE: INVALID TOKEN
       ===================================================== */
    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        // ---------- Arrange ----------
        String invalidToken = "this-is-not-a-token";

        // ---------- Act & Assert ----------
        assertFalse(jwtUtil.validateToken(invalidToken, userDetails));
        assertThrows(JwtException.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    /* =====================================================
       NEGATIVE: EXPIRED TOKEN
       ===================================================== */
    @Test
    void validateToken_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
        // ---------- Arrange ----------
        String token = jwtUtil.generateToken(userDetails);

        // wait until token expires
        Thread.sleep(expirationMs + 10);

        // ---------- Act ----------
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // ---------- Assert ----------
        assertFalse(isValid);
    }

    /* =====================================================
       NEGATIVE: WRONG USER
       ===================================================== */
    @Test
    void validateToken_shouldReturnFalse_whenUsernameDoesNotMatch() {
        // ---------- Arrange ----------
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = new User("alice", "password", Collections.emptyList());

        // ---------- Act ----------
        boolean isValid = jwtUtil.validateToken(token, otherUser);

        // ---------- Assert ----------
        assertFalse(isValid);
    }

    /* =====================================================
       POSITIVE: EXTRACT USERNAME
       ===================================================== */
    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        // ---------- Arrange ----------
        String token = jwtUtil.generateToken(userDetails);

        // ---------- Act ----------
        String username = jwtUtil.extractUsername(token);

        // ---------- Assert ----------
        assertEquals("john", username);
    }
}