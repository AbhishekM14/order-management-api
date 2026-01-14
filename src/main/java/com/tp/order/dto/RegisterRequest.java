package com.tp.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tp.order.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        
        UserRole role
) {
    public RegisterRequest(String username, String email, String password) {
        this(username, email, password, UserRole.USER);
    }
    
    @Override
    public String toString() {
        return "RegisterRequest{username='" + username + "', email='" + maskEmail(email) + "', password='[PROTECTED]', role=" + role + "}";
    }
    
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "[PROTECTED]";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 2) {
            return "**@" + parts[1];
        }
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + parts[1];
    }
}
