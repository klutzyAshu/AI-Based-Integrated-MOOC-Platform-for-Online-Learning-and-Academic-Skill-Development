# Neolce

Neolce is a role-based online learning platform built with React, Spring Boot, and MariaDB. Learners can browse courses, enroll, and ask an AI assistant for help. Instructors can create and publish courses, while administrators manage users.

## Features

- JWT-based login and registration with student, instructor, and administrator roles
- Course catalog, enrollment tracking, instructor course management, and role-aware dashboards
- Course modules, interactive lesson viewing, quizzes, and persisted quiz attempts
- Lesson completion tracking with automatic certificates on course completion
- Instructor course builder and admin/instructor enrollment analytics
- AI assistant, recommendations, assessment generation, and study-plan request types
- Responsive web interface and Docker Compose development environment

## Technology

- **Frontend:** React 18, React Router, Axios, Vite
- **Backend:** Java 17, Spring Boot 3.3, Spring Data JPA, Spring Security, JWT
- **Database:** MariaDB 11.4
- **AI:** OpenAI-compatible chat completions API; optional standalone Python examples under `ai/`

## Run with Docker Compose

1. Copy `.env.example` to `.env` and set `DB_PASSWORD` and `JWT_SECRET`. Add `AI_API_KEY` to enable live AI responses.
2. Start the services:

   ```bash
   docker compose up --build
   ```

3. Open the frontend at [http://localhost:5173](http://localhost:5173). The API is at [http://localhost:8080/api](http://localhost:8080/api); health is at `/actuator/health`.

MariaDB initializes the schema on its first start. To load demo users and courses, run `bash scripts/seed_database.sh --with-sample-data` after the database is ready. The demo accounts use `Password123`: `admin@mooc.dev`, `instructor@mooc.dev`, and `student@mooc.dev`. These accounts are for local development only.

## Run services locally

Requirements: Java 17, Maven, Node.js 20+, npm, and MariaDB. The repository does not include a Maven wrapper.

1. Create the schema using `bash scripts/seed_database.sh` (set `DB_USERNAME` and `DB_PASSWORD` if needed).
2. Start the backend: `cd backend && mvn spring-boot:run`.
3. In another terminal, start the frontend: `cd frontend && npm install && npm run dev`.

For local frontend development, Vite proxies `/api` to `http://localhost:8080`. To use a different API URL, set `VITE_API_BASE_URL`. Spring reads environment variables; a `.env` file is loaded automatically by Docker Compose, but for a local Maven run export the values in your shell first.

## Repository map

```text
frontend/   React application, pages, shared components, and API clients
backend/    Spring Boot REST API, security, services, JPA models, and repositories
database/   MariaDB schema and optional sample data
ai/         Standalone Python AI experiments and prompt templates
docs/       Architecture, API reference, database design, and requirements
scripts/    Local setup and database seeding helpers
storage/    Runtime file storage mount point
```

See [`docs/architecture.md`](docs/architecture.md), [`docs/API.md`](docs/API.md), and [`docs/database-design.md`](docs/database-design.md) for implementation details.

```bash
git clone https://github.com/klutzyAshu/AI-Based-Integrated-MOOC-Platform-for-Online-Learning-and-Academic-Skill-Development
cd <project-folder>
```

## Configuration

See `.env.example` for database, JWT, AI provider, storage, and frontend settings. The default AI configuration returns a development placeholder when no API key is set. Use a strong, private `JWT_SECRET` outside local development.

## Project status

This is an active development project. Lesson progress, printable completion certificates, course and category authoring, and basic enrollment analytics are wired into the application. Broader reporting remains future work.
