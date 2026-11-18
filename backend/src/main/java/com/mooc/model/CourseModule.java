package com.mooc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A module groups a set of ordered lessons within a course
 * (e.g. "Week 1: Introduction").
 */
@Entity
@Table(name = "course_modules")
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "module_order")
    private Integer order = 0;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<Lesson> lessons = new ArrayList<>();

    public CourseModule() {
    }

    public CourseModule(Long id, Course course, String title, Integer order, List<Lesson> lessons) {
        this.id = id;
        this.course = course;
        this.title = title;
        this.order = order != null ? order : 0;
        this.lessons = lessons != null ? lessons : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public static CourseModuleBuilder builder() {
        return new CourseModuleBuilder();
    }

    public static class CourseModuleBuilder {
        private Long id;
        private Course course;
        private String title;
        private Integer order = 0;
        private List<Lesson> lessons = new ArrayList<>();

        CourseModuleBuilder() {
        }

        public CourseModuleBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CourseModuleBuilder course(Course course) {
            this.course = course;
            return this;
        }

        public CourseModuleBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CourseModuleBuilder order(Integer order) {
            this.order = order;
            return this;
        }

        public CourseModuleBuilder lessons(List<Lesson> lessons) {
            this.lessons = lessons;
            return this;
        }

        public CourseModule build() {
            return new CourseModule(id, course, title, order, lessons);
        }
    }
}
