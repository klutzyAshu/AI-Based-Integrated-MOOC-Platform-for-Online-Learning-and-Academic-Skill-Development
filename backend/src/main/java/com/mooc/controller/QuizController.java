package com.mooc.controller;

import com.mooc.dto.QuizRequest;
import com.mooc.model.Quiz;
import com.mooc.model.QuizAttempt;
import com.mooc.model.User;
import com.mooc.repository.QuizRepository;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<Quiz>> byLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(quizService.findByLesson(lessonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> byId(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Quiz> create(@RequestBody QuizRequest request) {
        return ResponseEntity.ok(quizService.create(request));
    }

    @SneakyThrows
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submit(@PathVariable Long id,
                                                        @RequestBody List<Integer> answers,
                                                        @AuthenticationPrincipal AppUserPrincipal principal) {
        Quiz quiz = quizService.findById(id);
        double score = quizService.grade(quiz, answers);
        boolean passed = score >= quiz.getPassingScore();

        QuizAttempt attempt = QuizAttempt.builder()
                .user(principal.getUser())
                .quiz(quiz)
                .score(score)
                .passed(passed)
                .answersJson(objectMapper.writeValueAsString(answers))
                .build();

        return ResponseEntity.ok(Map.of(
                "score", score,
                "passed", passed,
                "passingScore", quiz.getPassingScore()
        ));
    }
}
