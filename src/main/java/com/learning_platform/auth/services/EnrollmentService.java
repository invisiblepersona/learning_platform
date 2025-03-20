package com.learning_platform.auth.services;

import com.learning_platform.auth.models.Enrollment;
import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.CourseRepository;
import com.learning_platform.auth.repositories.EnrollmentRepository;
import com.learning_platform.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // ✅ Student enrolls in a course
    public Enrollment enrollStudent(String studentEmail, Long courseId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return enrollmentRepository.save(enrollment);
    }

    // ✅ Get all enrollments for a student
    public List<Enrollment> getEnrollmentsByStudent(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student);
    }
}
