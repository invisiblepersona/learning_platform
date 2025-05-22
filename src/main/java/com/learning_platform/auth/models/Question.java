package com.learning_platform.auth.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String correctAnswer;

    @ElementCollection
    private List<String> options;

    @ManyToOne(optional = false)
    private Quiz quiz;
}
