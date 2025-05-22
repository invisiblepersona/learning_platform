package com.learning_platform.auth.services;

import com.learning_platform.auth.dto.AuthRequest;
import com.learning_platform.auth.dto.AuthResponse;
import com.learning_platform.auth.models.PasswordResetToken;
import com.learning_platform.auth.models.Role;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.PasswordResetTokenRepository;
import com.learning_platform.auth.repositories.UserRepository;
import com.learning_platform.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository; // ✅ Injected
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // ✅ Authenticate user and return JWT token
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

    // ✅ Register new user
    public User registerUser(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT);

        return userRepository.save(user);
    }

    // ✅ Create and save reset token
   public String createPasswordResetToken(User user) {
    // 🧹 Delete any existing token for this user
    tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

    String token = UUID.randomUUID().toString();
    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setToken(token);
    resetToken.setUser(user);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, 30); // 30-minute expiration
    resetToken.setExpiryDate(cal.getTime());

    tokenRepository.save(resetToken);
    return token;
}

    // ✅ Reset password if token is valid
    public String resetPassword(String token, String newPassword) {
        com.learning_platform.auth.models.PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        return "Password reset successfully";
    }

    public List<User> getAllUsers() {
       return userRepository.findAll(); // or however you're fetching them
    }

    public User findById(Long id) {
        return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User save(User user) {
      return userRepository.save(user);
    }

    public void resetPasswordByAdmin(Long userId, String newPassword) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }


}
