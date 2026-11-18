package com.mooc.service;

import com.mooc.dto.ModuleRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Course;
import com.mooc.model.CourseModule;
import com.mooc.model.User;
import com.mooc.repository.CourseModuleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseModuleService {
    private final CourseModuleRepository moduleRepository;
    private final CourseService courseService;

    public CourseModuleService(CourseModuleRepository moduleRepository, CourseService courseService) {
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public List<CourseModule> findByCourse(Long courseId, User requester) {
        Course course = courseService.findById(courseId);
        if (!course.isPublished()) assertCanManage(course, requester);
        return moduleRepository.findByCourseIdOrderByOrderAsc(courseId);
    }

    @Transactional
    public CourseModule create(Long courseId, ModuleRequest request, User requester) {
        Course course = courseService.findById(courseId);
        assertCanManage(course, requester);
        CourseModule module = CourseModule.builder().course(course).title(request.title().trim())
                .order(request.order() == null ? 0 : request.order()).build();
        return moduleRepository.save(module);
    }

    @Transactional
    public CourseModule update(Long moduleId, ModuleRequest request, User requester) {
        CourseModule module = findModule(moduleId);
        assertCanManage(module.getCourse(), requester);
        if (request.title() != null && !request.title().isBlank()) module.setTitle(request.title().trim());
        if (request.order() != null) module.setOrder(request.order());
        return moduleRepository.save(module);
    }

    @Transactional
    public void delete(Long moduleId, User requester) {
        CourseModule module = findModule(moduleId);
        assertCanManage(module.getCourse(), requester);
        moduleRepository.delete(module);
    }

    @Transactional(readOnly = true)
    public CourseModule findModule(Long id) {
        return moduleRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Module", id));
    }

    private void assertCanManage(Course course, User requester) {
        if (requester.getRole() != User.Role.ADMIN &&
                (course.getInstructor() == null || !course.getInstructor().getId().equals(requester.getId()))) {
            throw new AccessDeniedException("You can only manage modules in your own courses");
        }
    }
}
