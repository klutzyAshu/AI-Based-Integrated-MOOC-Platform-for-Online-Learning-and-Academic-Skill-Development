package com.mooc.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_lesson_progress", uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "lesson_id"}))
public class StudentLessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    public StudentLessonProgress() {}
    public StudentLessonProgress(User user, Lesson lesson) {
        this.user = user;
        this.lesson = lesson;
    }
    @PrePersist
    protected void onCreate() { completedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Lesson getLesson() { return lesson; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
