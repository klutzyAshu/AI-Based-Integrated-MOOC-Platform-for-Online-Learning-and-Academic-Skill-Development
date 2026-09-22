package com.mooc.service;

import com.mooc.dto.CourseRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Category;
import com.mooc.model.Course;
import com.mooc.model.User;
import com.mooc.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> findAllPublished() {
        return courseRepository.findByPublishedTrue();
    }

    public List<Course> findByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<Course> search(String keyword) {
        return courseRepository.findByTitleContainingIgnoreCase(keyword);
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

    public Course update(Long id, CourseRequest request) {
        Course course = findById(id);
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getPublished() != null) course.setPublished(request.getPublished());
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    public Course publish(Long id) {
        Course course = findById(id);
        course.setPublished(true);
        return courseRepository.save(course);
    }
}
