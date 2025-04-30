package com.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Min(value = 0, message = "Age must be valid")
    private Integer age;

    @NotNull(message = "Gender is required")
    private String gender;

    private String otp;
    private boolean isOtpVerified;
    private boolean isEnabled = false;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date otpGeneratedTime;
} 