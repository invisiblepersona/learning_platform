package com.learning_platform.auth.controllers;

import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping("/register")
    public ResponseEntity<Course> registerCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.registerCourse(course));
    }

    // ✅ Fix: Keep only ONE update method with @PutMapping
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody Course courseDetails,
            @RequestHeader("Authorization") String token) {

        Course updatedCourse = courseService.updateCourse(id, courseDetails, token);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        courseService.deleteCourse(id, token);
        return ResponseEntity.ok("Course deleted successfully");
    }
}
