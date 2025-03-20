package com.learning_platform.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getInstructorDashboard() {
        return ResponseEntity.ok("✅ Welcome to the Instructor Dashboard!");
    }
}
