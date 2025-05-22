package com.learning_platform.auth.services;

import com.learning_platform.auth.models.*;
import com.learning_platform.auth.repositories.*;
import com.learning_platform.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JwtUtil jwtUtil;

    public Quiz createQuizForCourse(Long courseId, Quiz quiz, String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructorEmail().equals(email)) {
            throw new RuntimeException("Not authorized to create quiz for this course");
        }

        quiz.setCourse(course);
        quiz.getQuestions().forEach(q -> q.setQuiz(quiz));
        return quizRepository.save(quiz);
    }

    public List<Quiz> getQuizzesForStudent(Long courseId, String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        User student = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean enrolled = enrollmentRepository.existsByCourseIdAndStudentId(courseId, student.getId());
        if (!enrolled) throw new RuntimeException("Not enrolled in this course");

        return quizRepository.findByCourseId(courseId);
    }

    public List<Quiz> getQuizzesByCourse(Long courseId, String token) {
    String instructorEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new RuntimeException("Course not found"));

    if (!course.getInstructorEmail().equals(instructorEmail)) {
        throw new RuntimeException("Unauthorized to view quizzes for this course");
    }

    return quizRepository.findByCourseId(courseId);
}


}
