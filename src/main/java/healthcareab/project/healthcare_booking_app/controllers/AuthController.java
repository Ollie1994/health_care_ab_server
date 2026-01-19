package healthcareab.project.healthcare_booking_app.controllers;

import healthcareab.project.healthcare_booking_app.dto.AuthRequest;
import healthcareab.project.healthcare_booking_app.dto.AuthResponse;
import healthcareab.project.healthcare_booking_app.dto.RegisterRequest;
import healthcareab.project.healthcare_booking_app.dto.RegisterResponse;
import healthcareab.project.healthcare_booking_app.models.Role;
import healthcareab.project.healthcare_booking_app.models.User;
import healthcareab.project.healthcare_booking_app.services.AuthService;
import healthcareab.project.healthcare_booking_app.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        if(authService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setSocialSecurityNumber(registerRequest.getSocialSecurityNumber());
        // ADDING isANO
        user.setIsAnonymous(false);

        if(registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.PATIENT));
        } else {
            user.setRoles(registerRequest.getRoles());
        }

        authService.registerUser(user);

        RegisterResponse response = new RegisterResponse(
                "User registered successfully",
                user.getUsername(),
                user.getRoles(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getSocialSecurityNumber()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.generateToken(userDetails);
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true) // prevents javascript to get cookie
                    .secure(false) //IMPORTANT TO CHANGE IN PRODUCTION TO TRUE
                    .path("/")  // cookies is available in all application
                    .maxAge(10 * 60 * 60) // valid for 10h
                    .sameSite("Strict") // Lax & None
                    .build();

            AuthResponse authResponse = new AuthResponse(
                    "Login successful",
                    userDetails.getUsername(),
                    authService.findByUsername(userDetails.getUsername()).getRoles(),
                    authService.findByUsername(userDetails.getUsername()).getEmail(),
                    authService.findByUsername(userDetails.getUsername()).getFirstName(),
                    authService.findByUsername(userDetails.getUsername()).getLastName(),
                    authService.findByUsername(userDetails.getUsername()).getSocialSecurityNumber()

            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(authResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // VIKTIGT! ändra i production
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("Logout successful!");
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated!");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = authService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                "Authenticated",
                user.getUsername(),
                user.getRoles(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getSocialSecurityNumber()
        ));
    }

}
