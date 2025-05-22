package com.learning_platform.auth.services;

import com.learning_platform.auth.models.Course;
import com.learning_platform.auth.models.CourseMaterial;
import com.learning_platform.auth.repositories.CourseMaterialRepository;
import com.learning_platform.auth.repositories.CourseRepository;
import com.learning_platform.auth.repositories.EnrollmentRepository;
import com.learning_platform.auth.repositories.UserRepository;
import com.learning_platform.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learning_platform.auth.models.User;

import java.nio.file.Files;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final JwtUtil jwtUtil; // ✅ Inject JwtUtil for token handling
     private final UserRepository userRepository;
     private final CourseMaterialRepository courseMaterialRepository;
     private final EnrollmentRepository enrollmentRepository;
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
    User instructor = userRepository.findByEmail(course.getInstructorEmail())
        .orElseThrow(() -> new RuntimeException("Instructor not found"));

    if (!instructor.getRole().name().equals("ROLE_INSTRUCTOR")) {
        throw new RuntimeException("User is not an instructor");
    }

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

    public List<Course> getCoursesByToken(String token) {
    String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
    return courseRepository.findByInstructorEmail(email);
}

public Course createCourseAsInstructor(Course course, String token) {
    String instructorEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
    course.setInstructorEmail(instructorEmail);
    return courseRepository.save(course);
}

 public CourseMaterial uploadMaterial(Long courseId, MultipartFile file, String title, String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructorEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to upload to this course");
        }

        try {
            // Create uploads directory if it doesn't exist
            String uploadsDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadsDir);
            if (!dir.exists()) dir.mkdirs();

            // Construct safe filename and destination path
            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            File destination = new File(dir, filename);

            // Save the file to disk
            file.transferTo(destination);

            // Save metadata to DB
            CourseMaterial material = new CourseMaterial();
            material.setTitle(title != null ? title : originalFilename);
            material.setFilePath(destination.getAbsolutePath());
            material.setType(Files.probeContentType(destination.toPath()));
            material.setUploadedAt(new Date());
            material.setCourse(course);

            return courseMaterialRepository.save(material);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public List<CourseMaterial> getMaterialsForCourse(Long courseId, String token) {
    String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new RuntimeException("Course not found"));

    if (!course.getInstructorEmail().equals(email)) {
        throw new RuntimeException("Unauthorized access to course materials");
    }

    return courseMaterialRepository.findByCourse(course);
}


public void deleteMaterial(Long materialId, String token) {
    String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
    CourseMaterial material = courseMaterialRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));

    // Ensure the instructor owns the course
    if (!material.getCourse().getInstructorEmail().equals(email)) {
        throw new RuntimeException("Unauthorized to delete this material");
    }

    // Delete the file from disk
    File file = new File(material.getFilePath());
    if (file.exists()) file.delete();

    courseMaterialRepository.delete(material);
}


public List<CourseMaterial> getMaterialsForCourseAsStudent(Long courseId, String token) {
    String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
    User student = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Student not found"));

    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new RuntimeException("Course not found"));

    boolean enrolled = enrollmentRepository.existsByCourseIdAndStudentId(courseId, student.getId());
    if (!enrolled) throw new RuntimeException("Not enrolled in this course");

    return courseMaterialRepository.findByCourse(course);
}


}
