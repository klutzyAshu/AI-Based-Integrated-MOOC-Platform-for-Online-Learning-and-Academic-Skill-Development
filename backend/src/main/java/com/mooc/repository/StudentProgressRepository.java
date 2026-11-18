package com.mooc.repository;

import com.mooc.model.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {
    Optional<StudentProgress> findByUserIdAndCourseId(Long userId, Long courseId);
}
