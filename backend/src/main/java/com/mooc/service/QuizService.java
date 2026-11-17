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
            if (answer == null || answer < 0 || answer > 3) {
                throw new IllegalArgumentException("Answer indices must be between 0 and 3");
            }
            if (questions.get(i).getCorrectIndex().equals(answer)) {
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
