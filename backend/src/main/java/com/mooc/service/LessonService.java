package com.mooc.service;

import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.CourseModule;
import com.mooc.model.Lesson;
import com.mooc.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public List<Lesson> findByModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderAsc(moduleId);
    }

    public Lesson findById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Lesson", id));
    }

    public Lesson create(Long moduleId, String title, String content, String videoUrl, Integer order) {
        CourseModule module = new CourseModule();
        module.setId(moduleId);

        Lesson lesson = Lesson.builder()
                .module(module)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .order(order != null ? order : 0)
                .build();

        return lessonRepository.save(lesson);
    }

    public Lesson update(Long id, String title, String content, String videoUrl) {
        Lesson lesson = findById(id);
        if (title != null) lesson.setTitle(title);
        if (content != null) lesson.setContent(content);
        if (videoUrl != null) lesson.setVideoUrl(videoUrl);
        return lessonRepository.save(lesson);
    }

    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }
}
