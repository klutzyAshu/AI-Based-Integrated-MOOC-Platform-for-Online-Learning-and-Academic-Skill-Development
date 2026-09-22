# Project Report

## AI-Based Integrated MOOC Platform for Online Learning and Academic Skill Development

### Abstract
This project delivers a unified online learning platform that merges
course delivery, assessment, progress tracking, and an AI-powered
learning assistant into a single system, addressing the fragmentation
of existing MOOC tools and the lack of real-time, personalized support
for learners.

### Objectives
- Provide a single platform for structured courses, quizzes, and
  progress tracking, replacing scattered tools.
- Integrate an AI layer offering real-time doubt resolution,
  personalized recommendations, and adaptive study plans.
- Support three distinct roles (Student, Instructor, Admin) with
  appropriate permissions and dashboards.

### Methodology
The system was built as a three-tier web application:
- **Frontend**: React (Vite) single-page application with role-based routing.
- **Backend**: Java + Spring Boot REST API with JWT-based authentication
  and role-based authorization.
- **Database**: MariaDB (MySQL-compatible) with a normalized relational schema.
- **AI Layer**: A dedicated service (`AIService`) that routes requests
  to an LLM provider for chatbot, recommendation, assessment
  generation, and personalization, with equivalent standalone Python
  scripts under `ai/` for local experimentation.

### Key Features Implemented
1. Registration/login with hashed passwords and JWT sessions.
2. Course authoring: courses → modules → lessons, with publish workflow.
3. Enrollment with duplicate-prevention and status tracking.
4. Quiz creation and auto-graded submissions against a configurable
   passing score.
5. An AI assistant reachable from any course page, plus recommendation
   and personalization endpoints.
6. Admin tooling to manage users and roles.

### Results
A working end-to-end flow was implemented and verified conceptually
end-to-end: a student can register, browse published courses, enroll,
work through lessons, take a quiz, and ask the AI assistant a question
— all backed by a real relational schema and a secured REST API.

### Future Work
- AI-generated quizzes wired directly into the instructor authoring flow.
- Adaptive study plans surfaced on the student dashboard.
- Deeper learning analytics for instructors and admins.
- Automated certificate PDF generation.
- Native mobile client.

### Repository Structure
See the root `README.md` and `docs/architecture.md` for the full
directory layout and system design.
