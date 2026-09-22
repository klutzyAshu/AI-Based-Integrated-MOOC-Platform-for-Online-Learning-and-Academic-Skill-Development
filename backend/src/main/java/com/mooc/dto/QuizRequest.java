package com.mooc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {

    @NotBlank
    private String title;

    private Long lessonId;

    private Integer passingScore;

    private List<QuestionItem> questions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionItem {
        private String text;
        private List<String> options;
        private Integer correctIndex;
        private String explanation;
    }
}
