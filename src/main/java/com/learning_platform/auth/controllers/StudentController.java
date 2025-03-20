package com.learning_platform.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getStudentDashboard(@RequestHeader("Authorization") String authHeader) {
        System.out.println("📌 Received Token: " + authHeader);
        return ResponseEntity.ok("✅ Welcome to the Student Dashboard!");
    }
}
