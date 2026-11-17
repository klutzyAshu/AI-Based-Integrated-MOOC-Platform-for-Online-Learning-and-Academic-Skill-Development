package com.mooc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "passing_score")
    private Integer passingScore = 60;

    @Column(name = "ai_generated")
    private boolean aiGenerated = false;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public Quiz() {
    }

    public Quiz(Long id, Lesson lesson, String title, Integer passingScore, boolean aiGenerated, List<Question> questions) {
        this.id = id;
        this.lesson = lesson;
        this.title = title;
        this.passingScore = passingScore != null ? passingScore : 60;
        this.aiGenerated = aiGenerated;
        this.questions = questions != null ? questions : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public boolean isAiGenerated() {
        return aiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        this.aiGenerated = aiGenerated;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public static QuizBuilder builder() {
        return new QuizBuilder();
    }

    public static class QuizBuilder {
        private Long id;
        private Lesson lesson;
        private String title;
        private Integer passingScore = 60;
        private boolean aiGenerated = false;
        private List<Question> questions = new ArrayList<>();

        QuizBuilder() {
        }

        public QuizBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuizBuilder lesson(Lesson lesson) {
            this.lesson = lesson;
            return this;
        }

        public QuizBuilder title(String title) {
            this.title = title;
            return this;
        }

        public QuizBuilder passingScore(Integer passingScore) {
            this.passingScore = passingScore;
            return this;
        }

        public QuizBuilder aiGenerated(boolean aiGenerated) {
            this.aiGenerated = aiGenerated;
            return this;
        }

        public QuizBuilder questions(List<Question> questions) {
            this.questions = questions;
            return this;
        }

        public Quiz build() {
            return new Quiz(id, lesson, title, passingScore, aiGenerated, questions);
        }
    }
}
