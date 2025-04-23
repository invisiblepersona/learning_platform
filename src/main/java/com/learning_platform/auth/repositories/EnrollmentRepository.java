package com.learning_platform.auth.repositories;

import com.learning_platform.auth.models.Enrollment;
import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);

    // ✅ Fixed: Check enrollment by Course ID and Student ID
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
}
