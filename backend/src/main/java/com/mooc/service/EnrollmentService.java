package com.mooc.service;

import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Course;
import com.mooc.model.Enrollment;
import com.mooc.model.User;
import com.mooc.repository.EnrollmentRepository;
import com.mooc.repository.LessonRepository;
import com.mooc.repository.StudentLessonProgressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final StudentLessonProgressRepository lessonProgressRepository;
    private final CertificateService certificateService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, LessonRepository lessonRepository,
                             StudentLessonProgressRepository lessonProgressRepository,
                             CertificateService certificateService) {
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.certificateService = certificateService;
    }

    public Enrollment enroll(User user, Course course) {
        if (user.getRole() != User.Role.STUDENT) {
            throw new org.springframework.security.access.AccessDeniedException("Only students can enroll in courses");
        }
        if (!course.isPublished()) {
            throw new IllegalArgumentException("This course is not available for enrollment");
        }
        var existing = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        if (existing.isPresent() && existing.get().getStatus() != Enrollment.Status.DROPPED) {
            throw new IllegalArgumentException("Already enrolled in this course");
        }
        if (existing.isPresent()) {
            Enrollment enrollment = existing.get();
            enrollment.setStatus(Enrollment.Status.ACTIVE);
            enrollment.setCompletedAt(null);
            return enrollmentRepository.save(enrollment);
        }
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(Enrollment.Status.ACTIVE)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> findByUser(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public List<Enrollment> findByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public Enrollment markCompleted(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        if (enrollment.getStatus() == Enrollment.Status.DROPPED) {
            throw new org.springframework.security.access.AccessDeniedException("Re-enroll before completing this course");
        }
        int lessonCount = lessonRepository.findByCourseId(courseId).size();
        int completedCount = lessonProgressRepository.findCompletedLessonIds(userId, courseId).size();
        if (lessonCount == 0 || completedCount != lessonCount) {
            throw new IllegalArgumentException("Complete every lesson before completing this course");
        }
        enrollment.setStatus(Enrollment.Status.COMPLETED);
        enrollment.setCompletedAt(LocalDateTime.now());
        Enrollment completed = enrollmentRepository.save(enrollment);
        certificateService.issue(enrollment.getUser(), enrollment.getCourse());
        return completed;
    }

    public void drop(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        enrollment.setStatus(Enrollment.Status.DROPPED);
        enrollmentRepository.save(enrollment);
    }
}
