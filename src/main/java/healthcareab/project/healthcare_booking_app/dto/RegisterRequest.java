package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Set<Role> roles;
    private String email;
    private String firstName;
    private String lastName;
    private String socialSecurityNumber;


    public RegisterRequest(String username, String password, Set<Role> roles, String email, String firstName, String lastName, String socialSecurityNumber) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public @NotBlank String getUsername() {
        return username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }
}
