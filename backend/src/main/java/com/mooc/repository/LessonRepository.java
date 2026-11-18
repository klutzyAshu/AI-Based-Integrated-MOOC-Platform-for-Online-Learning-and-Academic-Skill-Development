package com.mooc.repository;

import com.mooc.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleIdOrderByOrderAsc(Long moduleId);

    @Query("select l from Lesson l where l.module.course.id = :courseId order by l.module.order, l.order")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);
}
