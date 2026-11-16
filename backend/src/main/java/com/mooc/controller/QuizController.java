package com.mooc.controller;

import com.mooc.dto.QuizRequest;
import com.mooc.model.Quiz;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.QuizService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submit(@PathVariable Long id,
                                                        @RequestBody List<Integer> answers,
                                                        @AuthenticationPrincipal AppUserPrincipal principal) {
        Quiz quiz = quizService.findById(id);
        var attempt = quizService.recordAttempt(quiz, principal.getUser(), answers);

        return ResponseEntity.ok(Map.of(
                "score", attempt.getScore(),
                "passed", attempt.isPassed(),
                "passingScore", quiz.getPassingScore()
        ));
    }
}
