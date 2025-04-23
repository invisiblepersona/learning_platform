package com.learning_platform.auth.services;

import com.learning_platform.auth.dto.AuthRequest;
import com.learning_platform.auth.dto.AuthResponse;
import com.learning_platform.auth.models.Role;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.UserRepository;
import com.learning_platform.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // ✅ Login: authenticate using AuthenticationManager
    public AuthResponse login(AuthRequest request) {
        System.out.println("📌 Attempting login for: " + request.getEmail());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        System.out.println("✅ JWT Token Generated: " + token);
        return new AuthResponse(token);
    }

    // ✅ Registration: encode password securely
    public User registerUser(AuthRequest request) {
        System.out.println("📌 Attempting to register user: " + request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("⚠️ User already exists!");
            throw new RuntimeException("User already exists!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ Secure password hashing
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT);

        System.out.println("✅ User created: " + user.getEmail() + " | Role: " + user.getRole());
        return userRepository.save(user);
    }
}
