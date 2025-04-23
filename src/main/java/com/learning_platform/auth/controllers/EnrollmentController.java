package com.learning_platform.auth.controllers;

import com.learning_platform.auth.models.Enrollment;
import com.learning_platform.auth.services.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    // ✅ Enroll using token
    @PostMapping("/{courseId}")
    public ResponseEntity<Enrollment> enrollStudent(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String token) {
        Enrollment enrollment = enrollmentService.enrollStudent(token, courseId);
        return ResponseEntity.ok(enrollment);
    }

    // ✅ Get enrollments for current user using token
    @GetMapping
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(
            @RequestHeader("Authorization") String token) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByToken(token);
        return ResponseEntity.ok(enrollments);
    }
}
