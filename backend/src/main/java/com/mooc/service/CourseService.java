package com.mooc.service;

import com.mooc.dto.CourseRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Category;
import com.mooc.model.Course;
import com.mooc.model.User;
import com.mooc.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(instructor)
                .level(request.getLevel() != null ? request.getLevel() : "BEGINNER")
                .published(Boolean.TRUE.equals(request.getPublished()))
                .build();

        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            course.setCategory(category);
        }

        return courseRepository.save(course);
    }

    public Course update(Long id, CourseRequest request, User requester) {
        Course course = findById(id);
        assertCanManage(course, requester);
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getPublished() != null) course.setPublished(request.getPublished());
        return courseRepository.save(course);
    }

    public void delete(Long id, User requester) {
        Course course = findById(id);
        assertCanManage(course, requester);
        courseRepository.delete(course);
    }

    public Course publish(Long id, User requester) {
        Course course = findById(id);
        assertCanManage(course, requester);
        course.setPublished(true);
        return courseRepository.save(course);
    }

    private void assertCanManage(Course course, User requester) {
        if (requester.getRole() != User.Role.ADMIN
                && (course.getInstructor() == null || !course.getInstructor().getId().equals(requester.getId()))) {
            throw new org.springframework.security.access.AccessDeniedException("You can only manage your own courses");
        }
    }
}
