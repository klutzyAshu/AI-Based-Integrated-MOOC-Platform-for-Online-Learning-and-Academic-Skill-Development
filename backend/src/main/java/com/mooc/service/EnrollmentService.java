package com.mooc.service;

import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Course;
import com.mooc.model.Enrollment;
import com.mooc.model.User;
import com.mooc.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
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
        enrollment.setStatus(Enrollment.Status.COMPLETED);
        enrollment.setCompletedAt(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    public void drop(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        enrollment.setStatus(Enrollment.Status.DROPPED);
        enrollmentRepository.save(enrollment);
    }
}
