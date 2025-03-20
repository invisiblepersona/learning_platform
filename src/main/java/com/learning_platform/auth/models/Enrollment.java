package com.learning_platform.auth.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enrollments")
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  // ✅ Link to the User entity (student)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne  // ✅ Link to the Course entity
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
