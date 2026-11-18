package com.mooc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooc.dto.QuizRequest;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.*;
import com.mooc.repository.QuizAttemptRepository;
import com.mooc.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuizService(QuizRepository quizRepository, QuizAttemptRepository quizAttemptRepository) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    public Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Quiz", id));
    }

    public List<Quiz> findByLesson(Long lessonId) {
        return quizRepository.findByLessonId(lessonId);
    }

    public Quiz create(QuizRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Quiz title is required");
        }
        if (request.getPassingScore() != null && (request.getPassingScore() < 0 || request.getPassingScore() > 100)) {
            throw new IllegalArgumentException("Passing score must be between 0 and 100");
        }
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Add at least one question to the quiz");
        }
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
        for (QuizRequest.QuestionItem item : request.getQuestions()) {
                if (item == null) throw new IllegalArgumentException("Quiz questions cannot be empty");
                if (item.getText() == null || item.getText().isBlank()) {
                    throw new IllegalArgumentException("Question text is required");
                }
                if (item.getOptions() == null || item.getOptions().size() < 2) {
                    throw new IllegalArgumentException("Each question needs at least two answer options");
                }
                if (item.getCorrectIndex() == null || item.getCorrectIndex() < 0 || item.getCorrectIndex() >= item.getOptions().size()) {
                    throw new IllegalArgumentException("The correct answer must reference an available option");
                }
                String optionsJson;
                try {
                    optionsJson = objectMapper.writeValueAsString(item.getOptions());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to serialize options to JSON", e);
                }
                Question q = Question.builder()
                        .quiz(quiz)
                        .text(item.getText())
                        .optionsJson(optionsJson)
                        .correctIndex(item.getCorrectIndex())
                        .explanation(item.getExplanation())
                        .build();
                questions.add(q);
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

        if (answers == null || answers.size() != questions.size()) {
            throw new IllegalArgumentException("Submit one answer for each question");
        }

        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            Integer answer = answers.get(i);
            int optionCount;
            try {
                optionCount = objectMapper.readTree(questions.get(i).getOptionsJson()).size();
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Quiz question contains invalid options", e);
            }
            if (answer == null || answer < 0 || answer >= optionCount) {
                throw new IllegalArgumentException("An answer is outside the available options");
            }
            if (Objects.equals(questions.get(i).getCorrectIndex(), answer)) {
                correct++;
            }
        }
        return (correct * 100.0) / questions.size();
    }

    public QuizAttempt recordAttempt(Quiz quiz, User user, List<Integer> answers) {
        double score = grade(quiz, answers);
        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .score(score)
                .passed(score >= quiz.getPassingScore())
                .answersJson(writeAnswers(answers))
                .build();
        return quizAttemptRepository.save(attempt);
    }

    private String writeAnswers(List<Integer> answers) {
        try {
            return objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize answers to JSON", e);
        }
    }
}
