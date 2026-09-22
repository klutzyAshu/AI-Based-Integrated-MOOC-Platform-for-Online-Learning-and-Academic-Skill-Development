# Assessment Service

Generates multiple-choice quiz questions from lesson content, and can
automatically evaluate free-text answers when needed.

## Files
- `generate_quiz.py` — turns lesson text into structured quiz JSON.

## Output contract
Returns a JSON array of question objects:
```json
[
  {
    "text": "What data structure underlies a queue?",
    "options": ["Array", "Linked List", "Either A or B", "Graph"],
    "correctIndex": 2,
    "explanation": "Queues can be implemented with either arrays or linked lists."
  }
]
```
This matches `QuizRequest.QuestionItem` on the backend, so results can
be posted directly to `POST /api/quizzes`.

## Local test
```bash
python generate_quiz.py --lesson-file lesson.txt --num-questions 5
```
