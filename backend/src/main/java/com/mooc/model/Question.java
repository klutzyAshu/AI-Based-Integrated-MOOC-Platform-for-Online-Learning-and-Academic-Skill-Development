package com.mooc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, length = 500)
    private String text;

    // Stored as JSON array of strings, e.g. ["Option A","Option B","Option C","Option D"]
    @Lob
    @Column(name = "options_json")
    private String optionsJson;

    @Column(name = "correct_index", nullable = false)
    private Integer correctIndex;

    @Column(length = 500)
    private String explanation;
}
