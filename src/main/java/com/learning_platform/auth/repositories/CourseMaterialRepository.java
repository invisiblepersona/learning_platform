package com.learning_platform.auth.repositories;

import com.learning_platform.auth.models.CourseMaterial;
import com.learning_platform.auth.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findByCourse(Course course);
}
