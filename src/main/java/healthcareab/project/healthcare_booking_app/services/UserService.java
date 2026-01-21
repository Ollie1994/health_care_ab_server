package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.exceptions.AccessDeniedException;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void anonymizeUser() {

        User user = authService.getAuthenticated();
        if (!user.getRoles().contains(Role.PATIENT)) {
            throw new AccessDeniedException("Access denied");
        }


        SecureRandom random = new SecureRandom();
        String newUsername;
        String RandomAndEncodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());


        do {
            long number = random.nextLong(1_000_000L);
            String suffix = String.format("%06d", number);
            newUsername = "Anonymous_" + suffix;

        } while (userRepository.findByUsername(newUsername).isPresent());

        user.setUsername(newUsername);
        user.setPassword(RandomAndEncodedPassword);
        user.setRoles(Collections.emptySet());
        user.setEmail(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setSocialSecurityNumber(null);
        user.setIsAnonymous(true);

        userRepository.save(user);

    }
}