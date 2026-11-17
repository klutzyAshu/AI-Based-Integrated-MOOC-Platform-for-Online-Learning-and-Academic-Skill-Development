package com.mooc.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private boolean passed = false;

    // JSON array of the selected option indices, one per question, in order
    @Lob
    @Column(name = "answers_json")
    private String answersJson;

    @Column(name = "attempted_at", updatable = false)
    private LocalDateTime attemptedAt;

    public QuizAttempt() {
    }

    public QuizAttempt(Long id, User user, Quiz quiz, Double score, boolean passed, String answersJson, LocalDateTime attemptedAt) {
        this.id = id;
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.passed = passed;
        this.answersJson = answersJson;
        this.attemptedAt = attemptedAt;
    }

    @PrePersist
    protected void onCreate() {
        attemptedAt = LocalDateTime.now();
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

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public static QuizAttemptBuilder builder() {
        return new QuizAttemptBuilder();
    }

    public static class QuizAttemptBuilder {
        private Long id;
        private User user;
        private Quiz quiz;
        private Double score;
        private boolean passed = false;
        private String answersJson;
        private LocalDateTime attemptedAt;

        QuizAttemptBuilder() {
        }

        public QuizAttemptBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuizAttemptBuilder user(User user) {
            this.user = user;
            return this;
        }

        public QuizAttemptBuilder quiz(Quiz quiz) {
            this.quiz = quiz;
            return this;
        }

        public QuizAttemptBuilder score(Double score) {
            this.score = score;
            return this;
        }

        public QuizAttemptBuilder passed(boolean passed) {
            this.passed = passed;
            return this;
        }

        public QuizAttemptBuilder answersJson(String answersJson) {
            this.answersJson = answersJson;
            return this;
        }

        public QuizAttemptBuilder attemptedAt(LocalDateTime attemptedAt) {
            this.attemptedAt = attemptedAt;
            return this;
        }

        public QuizAttempt build() {
            return new QuizAttempt(id, user, quiz, score, passed, answersJson, attemptedAt);
        }
    }
}
