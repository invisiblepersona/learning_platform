package com.learning_platform.auth.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.learning_platform.auth.models.CourseMaterial;
import com.learning_platform.auth.services.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor // ✅ Injects CourseService automatically
public class StudentController {

    private final CourseService courseService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> getStudentDashboard(@RequestHeader("Authorization") String authHeader) {
        System.out.println("📌 Received Token: " + authHeader);
        return ResponseEntity.ok("✅ Welcome to the Student Dashboard!");
    }

    @GetMapping("/course/{courseId}/materials")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<CourseMaterial>> getCourseMaterials(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String token) {
        List<CourseMaterial> materials = courseService.getMaterialsForCourseAsStudent(courseId, token);
        return ResponseEntity.ok(materials);
    }
}
