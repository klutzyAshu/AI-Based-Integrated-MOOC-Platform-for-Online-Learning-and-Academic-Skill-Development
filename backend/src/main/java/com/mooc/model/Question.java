package com.mooc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @Column(nullable = false, length = 500)
    private String text;

    // Stored as JSON array of strings, e.g. ["Option A","Option B","Option C","Option D"]
    @Lob
    @Column(name = "options_json")
    private String optionsJson;

    @Column(name = "correct_index", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer correctIndex;

    @Column(length = 500)
    private String explanation;

    public Question() {
    }

    public Question(Long id, Quiz quiz, String text, String optionsJson, Integer correctIndex, String explanation) {
        this.id = id;
        this.quiz = quiz;
        this.text = text;
        this.optionsJson = optionsJson;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptionsJson() {
        return optionsJson;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public static QuestionBuilder builder() {
        return new QuestionBuilder();
    }

    public static class QuestionBuilder {
        private Long id;
        private Quiz quiz;
        private String text;
        private String optionsJson;
        private Integer correctIndex;
        private String explanation;

        QuestionBuilder() {
        }

        public QuestionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuestionBuilder quiz(Quiz quiz) {
            this.quiz = quiz;
            return this;
        }

        public QuestionBuilder text(String text) {
            this.text = text;
            return this;
        }

        public QuestionBuilder optionsJson(String optionsJson) {
            this.optionsJson = optionsJson;
            return this;
        }

        public QuestionBuilder correctIndex(Integer correctIndex) {
            this.correctIndex = correctIndex;
            return this;
        }

        public QuestionBuilder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Question build() {
            return new Question(id, quiz, text, optionsJson, correctIndex, explanation);
        }
    }
}
