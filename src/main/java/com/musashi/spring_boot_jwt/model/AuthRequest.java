package com.musashi.spring_boot_jwt.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class AuthRequest {

    @Email(message = "Invalid email format!")
    private String email;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}",
            message = "It should be: lowercase + uppercase + at least one digit + at least 9 characters"
    )
    private String password;

    public AuthRequest(){

    }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuthRequest that = (AuthRequest) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
