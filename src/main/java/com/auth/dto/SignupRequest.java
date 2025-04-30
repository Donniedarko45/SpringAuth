package com.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String reEnteredPassword;

    @Min(value = 0, message = "Age must be valid")
    private Integer age;

    @NotNull(message = "Gender is required")
    private String gender;
} 