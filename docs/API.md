# API Reference

Base URL: `http://localhost:8080/api`

All authenticated endpoints require an `Authorization: Bearer <token>`
header, obtained from `/auth/login` or `/auth/register`.

---

## Auth

### POST /auth/register
Public. Creates a new user and returns a JWT.
```json
// Request
{ "name": "Sam Student", "email": "sam@example.com", "password": "Password123", "role": "STUDENT" }

// Response
{ "token": "...", "tokenType": "Bearer", "userId": 3, "name": "Sam Student", "email": "sam@example.com", "role": "STUDENT" }
```

### POST /auth/login
Public.
```json
{ "email": "sam@example.com", "password": "Password123" }
```

---

## Users

| Method | Path | Access | Description |
|---|---|---|---|
| GET | /users/me | Authenticated | Current user's profile |
| PUT | /users/me?name=... | Authenticated | Update own name |
| GET | /users | ADMIN | List all users |
| GET | /users/{id} | ADMIN | Get a user |
| PATCH | /users/{id}/role?role=INSTRUCTOR | ADMIN | Change a user's role |
| DELETE | /users/{id} | ADMIN | Deactivate a user |

---

## Courses

| Method | Path | Access | Description |
|---|---|---|---|
| GET | /courses/public | Public | List published courses |
| GET | /courses/public/search?q=java | Public | Search published courses |
| GET | /courses/{id} | Authenticated | Get course detail (modules, lessons) |
| POST | /courses | INSTRUCTOR/ADMIN | Create a course |
| PUT | /courses/{id} | INSTRUCTOR/ADMIN | Update a course |
| PATCH | /courses/{id}/publish | INSTRUCTOR/ADMIN | Publish a course |
| DELETE | /courses/{id} | INSTRUCTOR/ADMIN | Delete a course |
| GET | /courses/instructor/{instructorId} | INSTRUCTOR/ADMIN | Courses by instructor |

`CourseRequest` body:
```json
{ "title": "Intro to Java", "description": "...", "categoryId": 1, "level": "BEGINNER", "published": false }
```

---

## Lessons

| Method | Path | Access | Description |
|---|---|---|---|
| GET | /lessons/module/{moduleId} | Authenticated | Lessons in a module, ordered |
| GET | /lessons/{id} | Authenticated | Get a lesson |
| POST | /lessons | INSTRUCTOR/ADMIN | Create a lesson (query params: moduleId, title, content, videoUrl, order) |
| PUT | /lessons/{id} | INSTRUCTOR/ADMIN | Update a lesson |
| DELETE | /lessons/{id} | INSTRUCTOR/ADMIN | Delete a lesson |

---

## Quizzes

| Method | Path | Access | Description |
|---|---|---|---|
| GET | /quizzes/lesson/{lessonId} | Authenticated | Quizzes for a lesson |
| GET | /quizzes/{id} | Authenticated | Get a quiz with questions |
| POST | /quizzes | INSTRUCTOR/ADMIN | Create a quiz with questions |
| POST | /quizzes/{id}/submit | Authenticated | Submit answers, get score |

`QuizRequest` body:
```json
{
  "title": "Java Basics Quiz",
  "lessonId": 2,
  "passingScore": 60,
  "questions": [
    { "text": "What keyword defines a class?", "options": ["def", "class", "func", "struct"], "correctIndex": 1, "explanation": "Java uses 'class'." }
  ]
}
```

Submit body: a JSON array of selected option indices, in question order:
```json
[1, 0, 2]
```

---

## Enrollments

| Method | Path | Access | Description |
|---|---|---|---|
| POST | /enrollments/{courseId} | Authenticated | Enroll in a course |
| GET | /enrollments/me | Authenticated | My enrollments |
| GET | /enrollments/course/{courseId} | Authenticated | Enrollments for a course (instructor view) |
| PATCH | /enrollments/{courseId}/complete | Authenticated | Mark a course completed |
| DELETE | /enrollments/{courseId} | Authenticated | Drop a course |

---

## AI Assistant

### POST /ai/ask
Authenticated. Single entry point for all AI features.

```json
{
  "type": "CHATBOT",        // CHATBOT | RECOMMENDATION | ASSESSMENT | PERSONALIZATION
  "prompt": "Explain recursion with an example",
  "contextJson": "{\"courseId\": 1}"
}
```

Response:
```json
{ "type": "CHATBOT", "reply": "Recursion is when a function calls itself...", "dataJson": null }
```

For `ASSESSMENT`, the reply comes back in `dataJson` as a JSON array of
generated questions matching the `QuizRequest.QuestionItem` shape.

---

## Error Format

All errors share a common shape:
```json
{ "timestamp": "2026-09-22T10:00:00", "status": 404, "error": "Course not found with id: 99" }
```
Validation errors additionally include a `fields` map of field → message.
