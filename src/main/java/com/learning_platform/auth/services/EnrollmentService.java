package com.learning_platform.auth.services;

import com.learning_platform.auth.models.Enrollment;
import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.CourseRepository;
import com.learning_platform.auth.repositories.EnrollmentRepository;
import com.learning_platform.auth.repositories.UserRepository;
import com.learning_platform.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // ✅ Student enrolls in a course using JWT token
    public Enrollment enrollStudent(String token, Long courseId) {
        String studentEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
                System.out.println("✅ Enrolling student: " + student.getEmail() + " to course ID: " + courseId);

        if (enrollmentRepository.existsByCourseIdAndStudentId(course.getId(), student.getId())) {
            throw new RuntimeException("Already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return enrollmentRepository.save(enrollment);
    }

    // ✅ Get all enrollments for a student using JWT token
    public List<Enrollment> getEnrollmentsByStudentByToken(String token) {
        String studentEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student);
    }

    public List<Enrollment> getEnrollmentsByToken(String token) {
        String studentEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
             

        return enrollmentRepository.findByStudent(student);
    }
    
}