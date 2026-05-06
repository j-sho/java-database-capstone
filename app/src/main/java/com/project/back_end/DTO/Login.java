package com.project.back_end.DTO;

/**
 * Login DTO - Used for capturing login credentials from authentication requests.
 * The 'identifier' field serves as either an email (for Doctors/Patients) 
 * or a username (for Admins).
 */
public class Login {

    private String identifier;
    private String password;

    /**
     * Default constructor for JSON deserialization.
     */
    public Login() {}

    /**
     * Parameterized constructor for convenience.
     */
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters and Setters

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
