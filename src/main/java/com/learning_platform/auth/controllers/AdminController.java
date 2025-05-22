package com.learning_platform.auth.controllers;

import java.util.List;

import com.learning_platform.auth.dto.AuthRequest;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("✅ Welcome to the Admin Dashboard!");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/users/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
    User user = userService.findById(id);
    user.setActive(!user.isActive());
    userService.save(user);
    return ResponseEntity.ok("User " + (user.isActive() ? "activated" : "deactivated") + " successfully");
   }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody AuthRequest request) {
      try {
        User newUser = userService.registerUser(request);
        return ResponseEntity.ok("User created successfully: " + newUser.getEmail());
      } catch (RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
      }
}

@PostMapping("/users/{id}/reset-password")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> resetUserPassword(@PathVariable Long id, @RequestBody String newPassword) {
    try {
        userService.resetPasswordByAdmin(id, newPassword);
        return ResponseEntity.ok("Password reset successfully.");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
    }
}


}
