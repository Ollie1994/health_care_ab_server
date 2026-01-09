package healthcareab.project.healthcare_booking_app;


import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    private final Validator validator;

    public UserModelTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /* =====================================================
       POSITIVE: TEST CONSTRUCTORS & GETTERS/SETTERS
    ===================================================== */
    @Test
    void user_constructorAndGettersSetters_shouldWork() {
        // --- Arrange ---
        Set<Role> roles = Set.of(Role.USER);
        User user = new User("john", "Password1!", roles);

        // --- Act & Assert ---
        assertEquals("john", user.getUsername());
        assertEquals("Password1!", user.getPassword());
        assertEquals(roles, user.getRoles());

        // Test setters
        user.setUsername("alice");
        user.setPassword("NewPass1!");
        user.setRoles(Set.of(Role.ADMIN));
        user.setEmail("alice@example.com");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setAddress("123 Street");

        assertEquals("alice", user.getUsername());
        assertEquals("NewPass1!", user.getPassword());
        assertTrue(user.getRoles().contains(Role.ADMIN));
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("123 Street", user.getAddress());
    }

    /* =====================================================
       POSITIVE: EMPTY CONSTRUCTOR
    ===================================================== */
    @Test
    void user_emptyConstructor_shouldWork() {
        User user = new User();
        assertNotNull(user); // instance created successfully
    }

    /* =====================================================
       NEGATIVE: USERNAME EMPTY VALIDATION
    ===================================================== */
    @Test
    void user_shouldFailValidation_whenUsernameEmpty() {
        User user = new User();
        user.setUsername("");
        user.setPassword("Password1!");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    /* =====================================================
       NEGATIVE: PASSWORD PATTERN VALIDATION
    ===================================================== */
    @Test
    void user_shouldFailValidation_whenPasswordInvalid() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("nopattern"); // invalid, missing uppercase/number/special

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    /* =====================================================
       POSITIVE: PASSWORD VALID
    ===================================================== */
    @Test
    void user_shouldPassValidation_whenPasswordValid() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("Valid1!A"); // 8 chars, satisfies regex


        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }
}