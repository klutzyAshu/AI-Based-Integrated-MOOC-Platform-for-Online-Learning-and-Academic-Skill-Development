package com.mooc.service;

import com.mooc.dto.AnalyticsOverview;
import com.mooc.dto.InstructorCourseMetric;
import com.mooc.model.Enrollment;
import com.mooc.model.User;
import com.mooc.repository.CertificateRepository;
import com.mooc.repository.CourseRepository;
import com.mooc.repository.EnrollmentRepository;
import com.mooc.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;

    public AnalyticsService(UserRepository userRepository, CourseRepository courseRepository,
                            EnrollmentRepository enrollmentRepository, CertificateRepository certificateRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
    }

    public AnalyticsOverview overview() {
        return new AnalyticsOverview(userRepository.count(), userRepository.countByRole(User.Role.STUDENT),
                userRepository.countByRole(User.Role.INSTRUCTOR), courseRepository.count(),
                courseRepository.countByPublishedTrue(), enrollmentRepository.count(),
                enrollmentRepository.countByStatus(Enrollment.Status.COMPLETED), certificateRepository.count());
    }

    public List<InstructorCourseMetric> instructorCourses(User instructor) {
        return courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructor.getId()).stream().map(course -> {
            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
            return new InstructorCourseMetric(course.getId(), course.getTitle(), course.isPublished(), enrollments.size(),
                    enrollments.stream().filter(e -> e.getStatus() == Enrollment.Status.COMPLETED).count());
        }).toList();
    }
}
