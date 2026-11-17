package com.mooc.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "enrolled_at", updatable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Enrollment() {
    }

    public Enrollment(Long id, User user, Course course, Status status, LocalDateTime enrolledAt, LocalDateTime completedAt) {
        this.id = id;
        this.user = user;
        this.course = course;
        this.status = status != null ? status : Status.ACTIVE;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
    }

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public enum Status {
        ACTIVE, COMPLETED, DROPPED
    }

    public static EnrollmentBuilder builder() {
        return new EnrollmentBuilder();
    }

    public static class EnrollmentBuilder {
        private Long id;
        private User user;
        private Course course;
        private Status status = Status.ACTIVE;
        private LocalDateTime enrolledAt;
        private LocalDateTime completedAt;

        EnrollmentBuilder() {
        }

        public EnrollmentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EnrollmentBuilder user(User user) {
            this.user = user;
            return this;
        }

        public EnrollmentBuilder course(Course course) {
            this.course = course;
            return this;
        }

        public EnrollmentBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public EnrollmentBuilder enrolledAt(LocalDateTime enrolledAt) {
            this.enrolledAt = enrolledAt;
            return this;
        }

        public EnrollmentBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Enrollment build() {
            return new Enrollment(id, user, course, status, enrolledAt, completedAt);
        }
    }
}
