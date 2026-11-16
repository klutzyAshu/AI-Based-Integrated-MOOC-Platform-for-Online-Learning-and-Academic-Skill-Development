# System Architecture

## Overview

The platform is a modular, four-layer system: a React frontend, a
Spring Boot REST backend, a pluggable AI services layer, and a
MariaDB (MySQL-compatible) database. Each layer is independently deployable.

```
┌──────────────┐     REST/JSON     ┌──────────────┐     JDBC     ┌──────────────┐
│   Frontend   │ ───────────────▶ │   Backend     │ ───────────▶ │   Database   │
│  React (Vite)│ ◀─────────────── │  Spring Boot  │ ◀─────────── │   MariaDB    │
└──────────────┘                  └──────┬───────┘               └──────────────┘
                                          │
                                          │ HTTP / in-process call
                                          ▼
                                   ┌──────────────┐
                                   │  AI Services │
                                   │ chatbot, reco,│
                                   │ assessment,   │
                                   │ personalization│
                                   └──────────────┘
```

## Layers

### 1. Frontend (React + Vite)
- Role-aware routing (`ProtectedRoute`) for Student / Instructor / Admin views.
- `AuthContext` holds the logged-in user and JWT; `services/api.js`
  attaches the token to every request via an Axios interceptor.
- Pages are grouped by role under `src/pages/{auth,student,instructor,admin}`.

### 2. Backend (Spring Boot / Java)
- **Controller** layer: thin REST endpoints (`/api/...`), validation, and
  `@PreAuthorize` role checks.
- **Service** layer: business logic (enrollment rules, quiz grading,
  AI request routing).
- **Repository** layer: Spring Data JPA interfaces over the entities.
- **Security**: `JwtAuthenticationFilter` reads the `Authorization`
  header on every request; `SecurityConfig` wires role-based access
  rules (`/api/admin/**` → ADMIN, `/api/instructor/**` → INSTRUCTOR/ADMIN).

### 3. AI Services
- Exposed to the frontend through one endpoint: `POST /api/ai/ask`,
  with a `type` field routing to chatbot / recommendation / assessment
  / personalization behavior inside `AIService`.
- `AIService` calls a configured LLM provider (OpenAI-compatible chat
  completions API) and degrades gracefully to a placeholder response
  when no API key is set, so the rest of the app still works in dev.
- Equivalent standalone Python scripts live under `ai/` for local
  experimentation or a future microservice split
  (`ai/chatbot/chatbot.py`, `ai/recommendation/recommend.py`,
  `ai/assessment/generate_quiz.py`, `ai/personalization/study_plan.py`).

### 4. Database (MariaDB)
- Schema is split by concern under `database/schema/`
  (`users.sql`, `courses.sql`, `enrollments.sql`, `quizzes.sql`, `ai.sql`).
- `database/database.sql` creates the database and loads each schema
  file in the correct dependency order.
- `database/seed/sample_data.sql` provides demo users, a course, and
  lessons for local development.

## Request Flow Example: Asking the AI Assistant a Question

1. Student types a question on the course page (`CourseView.jsx`).
2. Frontend calls `POST /api/ai/ask` with `{ type: "CHATBOT", prompt, contextJson }`.
3. `JwtAuthenticationFilter` validates the token and sets the security context.
4. `AIController` delegates to `AIService.handle(...)`.
5. `AIService` calls the configured LLM provider with a system prompt
   plus the student's question and course context.
6. The reply is returned as JSON and rendered in the chat panel.

## Key Design Decisions

- **Stateless auth (JWT)** so the backend can scale horizontally.
- Request DTOs validate auth, course, quiz, and AI inputs. Some read
  endpoints still serialize JPA entities directly; sensitive fields such
  as password hashes and quiz correct answers are excluded from responses.
- **AI layer isolated behind one controller/service** so the provider
  (or a self-hosted model) can be swapped without touching frontend code.
- **Role-based route protection** on both the frontend (`ProtectedRoute`)
  and backend (`@PreAuthorize`) for defense in depth.
