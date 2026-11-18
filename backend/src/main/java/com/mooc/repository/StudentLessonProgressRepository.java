package com.mooc.repository;

import com.mooc.model.StudentLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentLessonProgressRepository extends JpaRepository<StudentLessonProgress, Long> {
    Optional<StudentLessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("select p.lesson.id from StudentLessonProgress p where p.user.id = :userId and p.lesson.module.course.id = :courseId")
    List<Long> findCompletedLessonIds(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
