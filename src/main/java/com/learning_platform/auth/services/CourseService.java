package com.learning_platform.auth.services;

import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.repositories.CourseRepository;
import com.learning_platform.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final JwtUtil jwtUtil; // ✅ Inject JwtUtil for token handling

    // ✅ Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ✅ Get courses by instructor email
    public List<Course> getCoursesByInstructor(String email) {
        return courseRepository.findByInstructorEmail(email);
    }

    // ✅ Create a new course
    public Course registerCourse(Course course) {
        return courseRepository.save(course);
    }

    // ✅ Update course with authorization check
    public Course updateCourse(Long id, Course courseDetails, String token) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // ✅ Extract user email & role from JWT
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

        // ✅ Ensure only the instructor or admin can update the course
        if (!course.getInstructorEmail().equals(email) && !role.equals("ROLE_ADMIN")) {
            throw new RuntimeException("You are not authorized to update this course");
        }

        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        return courseRepository.save(course);
    }

    // ✅ Delete course with authorization check
    public void deleteCourse(Long id, String token) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // ✅ Extract user email & role from JWT
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

        // ✅ Ensure only the instructor or admin can delete the course
        if (!course.getInstructorEmail().equals(email) && !role.equals("ROLE_ADMIN")) {
            throw new RuntimeException("You are not authorized to delete this course");
        }

        courseRepository.delete(course);
    }
}
