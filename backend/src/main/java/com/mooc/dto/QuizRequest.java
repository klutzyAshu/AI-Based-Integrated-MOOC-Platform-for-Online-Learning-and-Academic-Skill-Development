package com.mooc.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class QuizRequest {

    @NotBlank
    private String title;

    private Long lessonId;

    private Integer passingScore;

    private List<QuestionItem> questions;

    public QuizRequest() {
    }

    public QuizRequest(String title, Long lessonId, Integer passingScore, List<QuestionItem> questions) {
        this.title = title;
        this.lessonId = lessonId;
        this.passingScore = passingScore;
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public List<QuestionItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionItem> questions) {
        this.questions = questions;
    }

    public static class QuestionItem {
        private String text;
        private List<String> options;
        private Integer correctIndex;
        private String explanation;

        public QuestionItem() {
        }

        public QuestionItem(String text, List<String> options, Integer correctIndex, String explanation) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
            this.explanation = explanation;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
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
    }
}
