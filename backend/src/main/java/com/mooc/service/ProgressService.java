package com.mooc.service;

import com.mooc.dto.LessonProgressResponse;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.*;
import com.mooc.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentLessonProgressRepository lessonProgressRepository;
    private final StudentProgressRepository progressRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;

    public ProgressService(CourseRepository courseRepository, LessonRepository lessonRepository,
                           EnrollmentRepository enrollmentRepository,
                           StudentLessonProgressRepository lessonProgressRepository,
                           StudentProgressRepository progressRepository,
                           CertificateRepository certificateRepository,
                           CertificateService certificateService) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.progressRepository = progressRepository;
        this.certificateRepository = certificateRepository;
        this.certificateService = certificateService;
    }

    @Transactional(readOnly = true)
    public LessonProgressResponse get(User user, Long courseId) {
        requireEnrollment(user, courseId);
        return snapshot(user, courseId);
    }

    @Transactional
    public LessonProgressResponse visit(User user, Long courseId, Long lessonId) {
        requireEnrollment(user, courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", courseId));
        Lesson lesson = findCourseLesson(courseId, lessonId);
        StudentProgress progress = progressRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseGet(() -> StudentProgress.builder().user(user).course(course).build());
        progress.setLastLesson(lesson);
        List<Long> completed = lessonProgressRepository.findCompletedLessonIds(user.getId(), courseId);
        int total = lessonRepository.findByCourseId(courseId).size();
        progress.setCompletionPercent(total == 0 ? 0 : completed.size() * 100.0 / total);
        progressRepository.save(progress);
        return snapshot(user, courseId);
    }

    @Transactional
    public LessonProgressResponse complete(User user, Long courseId, Long lessonId) {
        requireEnrollment(user, courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", courseId));
        Lesson lesson = findCourseLesson(courseId, lessonId);

        if (lessonProgressRepository.findByUserIdAndLessonId(user.getId(), lessonId).isEmpty()) {
            lessonProgressRepository.save(new StudentLessonProgress(user, lesson));
        }

        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        List<Long> completedIds = lessonProgressRepository.findCompletedLessonIds(user.getId(), courseId);
        double percent = lessons.isEmpty() ? 0 : completedIds.size() * 100.0 / lessons.size();
        StudentProgress progress = progressRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseGet(() -> StudentProgress.builder().user(user).course(course).build());
        progress.setCompletionPercent(percent);
        progress.setLastLesson(lesson);
        progressRepository.save(progress);

        if (!lessons.isEmpty() && completedIds.size() == lessons.size()) {
            enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId).ifPresent(enrollment -> {
                if (enrollment.getStatus() == Enrollment.Status.ACTIVE) {
                    enrollment.setStatus(Enrollment.Status.COMPLETED);
                    enrollment.setCompletedAt(java.time.LocalDateTime.now());
                    enrollmentRepository.save(enrollment);
                }
            });
            certificateService.issue(user, course);
        }
        return snapshot(user, courseId);
    }

    private LessonProgressResponse snapshot(User user, Long courseId) {
        List<Long> completed = lessonProgressRepository.findCompletedLessonIds(user.getId(), courseId);
        StudentProgress progress = progressRepository.findByUserIdAndCourseId(user.getId(), courseId).orElse(null);
        int lessonCount = lessonRepository.findByCourseId(courseId).size();
        double percent = lessonCount == 0 ? 0 : completed.size() * 100.0 / lessonCount;
        String certificateCode = certificateRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .map(Certificate::getCertificateCode).orElse(null);
        return new LessonProgressResponse(courseId, percent, completed,
                progress == null || progress.getLastLesson() == null ? null : progress.getLastLesson().getId(), certificateCode);
    }

    private void requireEnrollment(User user, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new AccessDeniedException("Enroll in this course to track progress"));
        if (enrollment.getStatus() == Enrollment.Status.DROPPED) {
            throw new AccessDeniedException("Re-enroll in this course to track progress");
        }
    }

    private Lesson findCourseLesson(Long courseId, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> ResourceNotFoundException.of("Lesson", lessonId));
        if (lesson.getModule() == null || lesson.getModule().getCourse() == null ||
                !courseId.equals(lesson.getModule().getCourse().getId())) {
            throw new IllegalArgumentException("Lesson does not belong to this course");
        }
        return lesson;
    }
}
