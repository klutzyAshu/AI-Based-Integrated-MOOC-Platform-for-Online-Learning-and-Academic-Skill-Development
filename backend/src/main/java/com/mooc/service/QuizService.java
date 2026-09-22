package com.mooc.service;

import com.mooc.dto.QuizRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.*;
import com.mooc.repository.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Quiz", id));
    }

    public List<Quiz> findByLesson(Long lessonId) {
        return quizRepository.findByLessonId(lessonId);
    }

    @SneakyThrows
    public Quiz create(QuizRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .passingScore(request.getPassingScore() != null ? request.getPassingScore() : 60)
                .build();

        if (request.getLessonId() != null) {
            Lesson lesson = new Lesson();
            lesson.setId(request.getLessonId());
            quiz.setLesson(lesson);
        }

        List<Question> questions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (QuizRequest.QuestionItem item : request.getQuestions()) {
                Question q = Question.builder()
                        .quiz(quiz)
                        .text(item.getText())
                        .optionsJson(objectMapper.writeValueAsString(item.getOptions()))
                        .correctIndex(item.getCorrectIndex())
                        .explanation(item.getExplanation())
                        .build();
                questions.add(q);
            }
        }
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    /**
     * Grades a submitted attempt: answers is a list of chosen option
     * indices, in the same order as quiz.getQuestions().
     */
    public double grade(Quiz quiz, List<Integer> answers) {
        List<Question> questions = quiz.getQuestions();
        if (questions.isEmpty()) return 0.0;

        int correct = 0;
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            if (questions.get(i).getCorrectIndex().equals(answers.get(i))) {
                correct++;
            }
        }
        return (correct * 100.0) / questions.size();
    }
}
