package com.learning_platform.auth.controllers;

import com.learning_platform.auth.dto.AuthRequest;
import com.learning_platform.auth.dto.AuthResponse;
import com.learning_platform.auth.dto.ResetPasswordRequest;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.UserRepository;
import com.learning_platform.auth.services.EmailService;
import com.learning_platform.auth.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailService emailService; // ✅ Injected properly

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.login(request);
    }

    @GetMapping("/test-db")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        User newUser = userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully with email: " + newUser.getEmail() + " and role: " + newUser.getRole());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendResetLink(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = userService.createPasswordResetToken(user);
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        emailService.sendEmail(user.getEmail(), "Reset Password", "Click the link to reset your password: " + resetLink);
        return ResponseEntity.ok("Reset email sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request.getToken(), request.getNewPassword()));
    }
}
