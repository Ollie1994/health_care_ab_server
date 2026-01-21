package healthcareab.project.healthcare_booking_app.services;

import healthcareab.project.healthcare_booking_app.exceptions.UnauthorizedException;
import healthcareab.project.healthcare_booking_app.models.ActionPerformed;
import healthcareab.project.healthcare_booking_app.helpers.sesEmail.SESEmailHelper;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SESEmailHelper sesEmailHelper;
    private final LogService logService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, SESEmailHelper sesEmailHelper, LogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sesEmailHelper = sesEmailHelper;
        this.logService = logService;
    }

    // register user
    public void registerUser(User user) {
        try {
        // hash password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // ensure the user has at least default role USER
        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.PATIENT));
        }

        if (user.getEmail() != null) {
            String message = "You have created an account with the username - " + user.getUsername();
            sesEmailHelper.sendEmail(message, "Registration Confirmation Email", user.getEmail());
        }


        userRepository.save(user);

        // Log successful CREATED_ACCOUNT
        logService.log(
                ActionPerformed.CREATED_ACCOUNT,
                user.getId(),
                user.getId(),
                true
        );

    } catch (Exception e) {

        // Log failed CREATED_ACCOUNT
        logService.log(
                ActionPerformed.CREATED_ACCOUNT,
                null,
                null,
                false
        );
        throw e;

      }

    }

    // find user by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // check if username already exists
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }


    public User getAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("User is not authenticated");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return findByUsername(userDetails.getUsername());
    }


}
