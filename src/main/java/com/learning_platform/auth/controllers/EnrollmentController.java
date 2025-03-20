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

    @PostMapping("/{studentEmail}/{courseId}")
    public Enrollment enrollStudent(@PathVariable String studentEmail, @PathVariable Long courseId) {
        return enrollmentService.enrollStudent(studentEmail, courseId);
    }

    @GetMapping("/{studentEmail}")
    public List<Enrollment> getEnrollmentsByStudent(@PathVariable String studentEmail) {
        return enrollmentService.getEnrollmentsByStudent(studentEmail);
    }
}
