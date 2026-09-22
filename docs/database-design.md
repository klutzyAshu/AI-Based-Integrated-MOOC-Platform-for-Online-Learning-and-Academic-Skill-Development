# Database Design

## Entity-Relationship Summary

```
users ──< courses (instructor_id)
users ──< enrollments >── courses
users ──< student_progress >── courses
users ──< quiz_attempts >── quizzes
users ──< certificates >── courses

categories ──< courses

courses ──< course_modules ──< lessons ──< quizzes ──< questions
```

## Tables

### users
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| name | VARCHAR(120) | |
| email | VARCHAR(150) | unique |
| password | VARCHAR(255) | BCrypt hash |
| role | ENUM | STUDENT / INSTRUCTOR / ADMIN |
| active | BOOLEAN | soft-deactivate flag |
| created_at / updated_at | DATETIME | |

### categories
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| name | VARCHAR(100) | unique |
| description | VARCHAR(255) | |

### courses
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| title | VARCHAR(200) | |
| description | TEXT | |
| instructor_id | BIGINT FK → users.id | |
| category_id | BIGINT FK → categories.id | |
| level | VARCHAR(30) | BEGINNER / INTERMEDIATE / ADVANCED |
| published | BOOLEAN | |

### course_modules
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| course_id | BIGINT FK → courses.id | cascade delete |
| title | VARCHAR(200) | |
| module_order | INT | ordering within course |

### lessons
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| module_id | BIGINT FK → course_modules.id | cascade delete |
| title | VARCHAR(200) | |
| content | LONGTEXT | |
| video_url | VARCHAR(500) | |
| lesson_order | INT | |
| duration_minutes | INT | |

### enrollments
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK → users.id | |
| course_id | BIGINT FK → courses.id | |
| status | ENUM | ACTIVE / COMPLETED / DROPPED |
| enrolled_at / completed_at | DATETIME | |
| — | UNIQUE (user_id, course_id) | prevents duplicate enrollment |

### student_progress
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| user_id / course_id | BIGINT FK | |
| completion_percent | DOUBLE | 0–100 |
| last_lesson_id | BIGINT FK → lessons.id | resume point |
| — | UNIQUE (user_id, course_id) | |

### quizzes / questions / quiz_attempts
- `quizzes` optionally attach to a `lesson_id`; `passing_score` is
  configurable (default 60).
- `questions.options_json` stores a JSON array of 4 option strings;
  `correct_index` is the 0-based correct answer.
- `quiz_attempts.answers_json` stores the learner's chosen indices,
  along with the computed `score` and `passed` flag.

### certificates
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| user_id / course_id | BIGINT FK | |
| certificate_code | VARCHAR(40) | unique, shareable/verifiable |
| file_url | VARCHAR(500) | generated PDF location |
| issued_at | DATETIME | |

### ai_chat_logs / ai_recommendation_logs (optional)
Audit tables for AI interactions, useful for improving prompts and
tracking usage; not required for core functionality.

## Notes
- All foreign keys use `ON DELETE CASCADE` where the child record is
  meaningless without its parent (e.g. lessons without a module), and
  `ON DELETE SET NULL` where the parent is optional context (e.g. a
  course's category).
- JPA (`spring.jpa.hibernate.ddl-auto=update`) can also generate/update
  this schema automatically in development; the SQL files in
  `database/schema/` are the source of truth for production.
