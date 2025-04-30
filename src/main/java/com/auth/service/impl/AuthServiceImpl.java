package com.auth.service.impl;

import com.auth.dto.LoginRequest;
import com.auth.dto.SignupRequest;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtTokenUtil;
import com.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender emailSender;
    private final UserDetailsService userDetailsService;

    @Override
    public String signup(SignupRequest request) {
        if (!validatePassword(request.getPassword(), request.getReEnteredPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setEnabled(false);
        
        userRepository.save(user);
        sendOtp(request.getEmail());
        
        return "User registered successfully. Please verify OTP sent to your email.";
    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email first");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if (request.getOtp() != null) {
            return verifyOtp(request.getUsername(), request.getOtp());
        }

        sendOtp(request.getUsername());
        return "OTP sent to your email";
    }

    @Override
    public String verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Check if OTP is expired (5 minutes validity)
        if (new Date().getTime() - user.getOtpGeneratedTime().getTime() > 300000) {
            throw new RuntimeException("OTP expired");
        }

        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public void sendOtp(String email) {
        String otp = generateOtp();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setOtp(otp);
        user.setOtpGeneratedTime(new Date());
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Authentication");
        message.setText("Your OTP is: " + otp);
        emailSender.send(message);
    }

    @Override
    public boolean validatePassword(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
} 