package com.mooc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Integer order = 0;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}
