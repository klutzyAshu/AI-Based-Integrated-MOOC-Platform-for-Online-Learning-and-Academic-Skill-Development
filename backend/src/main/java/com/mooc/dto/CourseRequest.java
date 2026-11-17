package com.mooc.dto;

import jakarta.validation.constraints.NotBlank;

public class CourseRequest {

    @NotBlank
    private String title;

    private String description;

    private Long categoryId;

    private String level;

    private Boolean published;

    public CourseRequest() {
    }

    public CourseRequest(String title, String description, Long categoryId, String level, Boolean published) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.level = level;
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
