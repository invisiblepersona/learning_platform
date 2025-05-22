package com.learning_platform.auth.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(optional = false)
    private Course course;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}