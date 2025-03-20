package com.learning_platform.auth.services;
import java.util.List;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        System.out.println("📌 Attempting login for: " + request.getEmail());
    
        // ✅ Fetch all users from DB
        List<User> allUsers = userRepository.findAll();
        System.out.println("📋 All Users in Database:");
        for (User u : allUsers) {
            System.out.println("🔹 " + u.getEmail());
        }
    
        // ✅ Fetch user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: " + request.getEmail());
                    return new RuntimeException("User not found");
                });
    
        System.out.println("✅ User found: " + user.getEmail());
    
        if (!request.getPassword().equals(user.getPassword())) {
            System.out.println("❌ Invalid password!");
            throw new RuntimeException("Invalid password");
        }
    
        System.out.println("🔓 Password matched successfully!");
    
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
    
        System.out.println("✅ JWT Token Generated: " + token);
    
        return new AuthResponse(token);
    }
    
    

    public User registerUser(AuthRequest request) {
        System.out.println("📌 Attempting to register user: " + request.getEmail());
    
        // ✅ Check if the email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("⚠️ User already exists!");
            throw new RuntimeException("User already exists!");
        }
    
        // ❌ Storing password as plain text (for testing only)
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());  // ❌ NO HASHING (Security Risk)
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT);
    
        System.out.println("✅ User created: " + user.getEmail() + " | Role: " + user.getRole());
    
        return userRepository.save(user);
    }
    
}

    

