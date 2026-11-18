package com.mooc.service;

import com.mooc.dto.CourseRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Category;
import com.mooc.model.Course;
import com.mooc.model.User;
import com.mooc.repository.CourseRepository;
import com.mooc.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Course> findAllPublished() {
        return courseRepository.findByPublishedTrue();
    }

    public List<Course> findByInstructor(Long instructorId, User requester) {
        if (requester.getRole() != User.Role.ADMIN && !requester.getId().equals(instructorId)) {
            throw new org.springframework.security.access.AccessDeniedException("You can only view your own courses");
        }
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<Course> search(String keyword) {
        return courseRepository.findByPublishedTrueAndTitleContainingIgnoreCase(keyword);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", id));
    }

    public Course create(CourseRequest request, User instructor) {
        if (Boolean.TRUE.equals(request.getPublished())) {
            throw new IllegalArgumentException("Create the course outline before publishing it");
        }
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(instructor)
                .level(request.getLevel() != null ? request.getLevel() : "BEGINNER")
                .published(Boolean.TRUE.equals(request.getPublished()))
                .build();

        if (request.getCategoryId() != null) {
            course.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId())));
        }

        return courseRepository.save(course);
    }

    @Transactional
    public Course update(Long id, CourseRequest request, User requester) {
        Course course = findById(id);
        assertCanManage(course, requester);
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getCategoryId() != null) {
            course.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Category", request.getCategoryId())));
        }
        if (Boolean.TRUE.equals(request.getPublished())) assertPublishable(course);
        if (request.getPublished() != null) course.setPublished(request.getPublished());
        return courseRepository.save(course);
    }

    public void delete(Long id, User requester) {
        Course course = findById(id);
        assertCanManage(course, requester);
        courseRepository.delete(course);
    }

    @Transactional
    public Course publish(Long id, User requester) {
        Course course = findById(id);
        assertCanManage(course, requester);
        assertPublishable(course);
        course.setPublished(true);
        return courseRepository.save(course);
    }

    private void assertPublishable(Course course) {
        if (course.getModules().isEmpty() || course.getModules().stream().allMatch(module -> module.getLessons().isEmpty())) {
            throw new IllegalArgumentException("Add at least one module and lesson before publishing this course");
        }
    }

    private void assertCanManage(Course course, User requester) {
        if (requester.getRole() != User.Role.ADMIN
                && (course.getInstructor() == null || !course.getInstructor().getId().equals(requester.getId()))) {
            throw new org.springframework.security.access.AccessDeniedException("You can only manage your own courses");
        }
    }
}
