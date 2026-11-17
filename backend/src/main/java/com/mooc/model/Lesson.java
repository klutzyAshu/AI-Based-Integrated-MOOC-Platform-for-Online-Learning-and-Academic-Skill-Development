package com.mooc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnore
    private CourseModule module;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "lesson_order")
    private Integer order = 0;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    public Lesson() {
    }

    public Lesson(Long id, CourseModule module, String title, String content, String videoUrl,
                  Integer order, Integer durationMinutes) {
        this.id = id;
        this.module = module;
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
        this.order = order != null ? order : 0;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseModule getModule() {
        return module;
    }

    public void setModule(CourseModule module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public static LessonBuilder builder() {
        return new LessonBuilder();
    }

    public static class LessonBuilder {
        private Long id;
        private CourseModule module;
        private String title;
        private String content;
        private String videoUrl;
        private Integer order = 0;
        private Integer durationMinutes;

        LessonBuilder() {
        }

        public LessonBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LessonBuilder module(CourseModule module) {
            this.module = module;
            return this;
        }

        public LessonBuilder title(String title) {
            this.title = title;
            return this;
        }

        public LessonBuilder content(String content) {
            this.content = content;
            return this;
        }

        public LessonBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public LessonBuilder order(Integer order) {
            this.order = order;
            return this;
        }

        public LessonBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Lesson build() {
            return new Lesson(id, module, title, content, videoUrl, order, durationMinutes);
        }
    }
}
