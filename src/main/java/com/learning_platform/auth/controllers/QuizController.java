package com.learning_platform.auth.controllers;

import com.learning_platform.auth.models.Quiz;
import com.learning_platform.auth.services.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/instructor/courses/{courseId}/quizzes")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<Quiz> createQuiz(
            @PathVariable Long courseId,
            @RequestBody Quiz quiz,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(quizService.createQuizForCourse(courseId, quiz, token));
    }

    @GetMapping("/student/courses/{courseId}/quizzes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<Quiz>> getQuizzes(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(quizService.getQuizzesForStudent(courseId, token));
    }
}
