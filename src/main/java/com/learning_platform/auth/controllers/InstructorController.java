package com.learning_platform.auth.controllers;

import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.models.CourseMaterial;
import com.learning_platform.auth.models.Quiz;
import com.learning_platform.auth.repositories.CourseMaterialRepository;
import com.learning_platform.auth.security.JwtUtil;
import com.learning_platform.auth.services.CourseService;
import com.learning_platform.auth.services.QuizService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    private final CourseService courseService;
    private final CourseMaterialRepository courseMaterialRepository;
    private final JwtUtil jwtUtil;
    private final QuizService quizService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> getInstructorDashboard() {
        return ResponseEntity.ok("✅ Welcome to the Instructor Dashboard!");
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getMyCourses(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(courseService.getCoursesByToken(token));
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(
            @RequestBody Course course,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(courseService.createCourseAsInstructor(course, token));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateMyCourse(
            @PathVariable Long id,
            @RequestBody Course course,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(courseService.updateCourse(id, course, token));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<String> deleteMyCourse(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        courseService.deleteCourse(id, token);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @PostMapping("/courses/{id}/materials")
    public ResponseEntity<CourseMaterial> uploadMaterial(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestHeader("Authorization") String token) {

        CourseMaterial material = courseService.uploadMaterial(id, file, title, token);
        return ResponseEntity.ok(material);
    }

    @GetMapping("/courses/{id}/materials")
    public ResponseEntity<List<CourseMaterial>> getMaterials(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(courseService.getMaterialsForCourse(id, token));
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<String> deleteMaterial(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        courseService.deleteMaterial(id, token);
        return ResponseEntity.ok("Material deleted successfully");
    }

   @GetMapping("/public/materials/{id}/download")
@PermitAll // or remove security on this endpoint
public ResponseEntity<Resource> downloadPublicMaterial(@PathVariable Long id) throws IOException {
    CourseMaterial material = courseMaterialRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Material not found"));

    Path path = Paths.get(material.getFilePath());
    Resource resource = new UrlResource(path.toUri());

    if (!resource.exists()) {
        throw new FileNotFoundException("File not found: " + path);
    }

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
        .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
        .body(resource);
}


@GetMapping("/courses/{courseId}/quizzes")
@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public ResponseEntity<List<Quiz>> getQuizzesByCourse(
        @PathVariable Long courseId,
        @RequestHeader("Authorization") String token) {
    List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId, token);
    return ResponseEntity.ok(quizzes);
}

}
