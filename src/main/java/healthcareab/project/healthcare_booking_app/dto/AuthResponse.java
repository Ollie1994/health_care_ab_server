package healthcareab.project.healthcare_booking_app.dto;

import healthcareab.project.healthcare_booking_app.models.Role;

import java.util.Set;

public class AuthResponse {
    private String jwtToken;
    private String username;
    private Set<Role> roles;
    private String email;
    private String firstName;
    private String lastName;
    private String address;

    public AuthResponse(String jwtToken, String username, Set<Role> roles, String email, String firstName, String lastName, String address) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.roles = roles;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }


    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
