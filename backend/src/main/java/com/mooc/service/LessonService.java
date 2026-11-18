package com.mooc.service;

import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.CourseModule;
import com.mooc.model.Lesson;
import com.mooc.repository.LessonRepository;
import com.mooc.repository.CourseModuleRepository;
import com.mooc.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseModuleRepository moduleRepository;

    public LessonService(LessonRepository lessonRepository, CourseModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional(readOnly = true)
    public List<Lesson> findByModule(Long moduleId, User requester) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> ResourceNotFoundException.of("Module", moduleId));
        assertCanView(module, requester);
        return lessonRepository.findByModuleIdOrderByOrderAsc(moduleId);
    }

    @Transactional(readOnly = true)
    public Lesson findById(Long id, User requester) {
        Lesson lesson = findById(id);
        assertCanView(lesson.getModule(), requester);
        return lesson;
    }

    public Lesson findById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Lesson", id));
    }

    @Transactional
    public Lesson create(Long moduleId, String title, String content, String videoUrl, Integer order, User requester) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> ResourceNotFoundException.of("Module", moduleId));
        assertCanManage(module, requester);

        Lesson lesson = Lesson.builder()
                .module(module)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .order(order != null ? order : 0)
                .build();

        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson update(Long id, String title, String content, String videoUrl, User requester) {
        Lesson lesson = findById(id);
        assertCanManage(lesson.getModule(), requester);
        if (title != null) lesson.setTitle(title);
        if (content != null) lesson.setContent(content);
        if (videoUrl != null) lesson.setVideoUrl(videoUrl);
        return lessonRepository.save(lesson);
    }

    @Transactional
    public void delete(Long id, User requester) {
        Lesson lesson = findById(id);
        assertCanManage(lesson.getModule(), requester);
        lessonRepository.delete(lesson);
    }

    private void assertCanManage(CourseModule module, User requester) {
        if (requester.getRole() != User.Role.ADMIN &&
                (module.getCourse().getInstructor() == null ||
                        !module.getCourse().getInstructor().getId().equals(requester.getId()))) {
            throw new AccessDeniedException("You can only manage lessons in your own courses");
        }
    }

    private void assertCanView(CourseModule module, User requester) {
        if (!module.getCourse().isPublished()) assertCanManage(module, requester);
    }
}
