package com.mooc.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Tracks a student's completion percentage and last-visited lesson
 * for a given course, used to resume learning and power dashboards.
 */
@Entity
@Table(name = "student_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "completion_percent")
    private Double completionPercent = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_lesson_id")
    private Lesson lastLesson;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public StudentProgress() {
    }

    public StudentProgress(Long id, User user, Course course, Double completionPercent, Lesson lastLesson, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.course = course;
        this.completionPercent = completionPercent != null ? completionPercent : 0.0;
        this.lastLesson = lastLesson;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void touch() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Double getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(Double completionPercent) {
        this.completionPercent = completionPercent;
    }

    public Lesson getLastLesson() {
        return lastLesson;
    }

    public void setLastLesson(Lesson lastLesson) {
        this.lastLesson = lastLesson;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static StudentProgressBuilder builder() {
        return new StudentProgressBuilder();
    }

    public static class StudentProgressBuilder {
        private Long id;
        private User user;
        private Course course;
        private Double completionPercent = 0.0;
        private Lesson lastLesson;
        private LocalDateTime updatedAt;

        StudentProgressBuilder() {
        }

        public StudentProgressBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentProgressBuilder user(User user) {
            this.user = user;
            return this;
        }

        public StudentProgressBuilder course(Course course) {
            this.course = course;
            return this;
        }

        public StudentProgressBuilder completionPercent(Double completionPercent) {
            this.completionPercent = completionPercent;
            return this;
        }

        public StudentProgressBuilder lastLesson(Lesson lastLesson) {
            this.lastLesson = lastLesson;
            return this;
        }

        public StudentProgressBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public StudentProgress build() {
            return new StudentProgress(id, user, course, completionPercent, lastLesson, updatedAt);
        }
    }
}
