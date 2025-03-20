package com.learning_platform.auth.controllers;

import com.learning_platform.auth.dto.AuthRequest;
import com.learning_platform.auth.dto.AuthResponse;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.UserRepository;
import com.learning_platform.auth.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // ✅ Ensures UserRepository is injected correctly
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository; // ✅ Injected repository

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.login(request);
    }

    @GetMapping("/test-db")
    public List<User> getAllUsers() {
        return userRepository.findAll();  // ✅ Fetch all users from the database
    }
    @PostMapping("/register")
public ResponseEntity<String> register(@RequestBody AuthRequest request) {
    User newUser = userService.registerUser(request);
    return ResponseEntity.ok("User registered successfully with email: " + newUser.getEmail()+" and role: " + newUser.getRole());
}
}
