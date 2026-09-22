# Software Requirements Specification (SRS)

## AI-Based Integrated MOOC Platform for Online Learning and Academic Skill Development

### 1. Introduction

#### 1.1 Purpose
This document specifies the functional and non-functional requirements
for a unified, AI-powered MOOC (Massive Open Online Course) platform
that combines course delivery, assessment, progress tracking, and an
intelligent learning assistant into one system.

#### 1.2 Scope
The platform supports three user roles — **Student**, **Instructor**,
and **Admin** — and covers:
- Course authoring and publishing
- Enrollment and progress tracking
- Quizzes and automated assessment
- An AI learning assistant (chatbot, recommendations, personalization)
- Certificates on course completion

#### 1.3 Intended Audience
Developers, project reviewers/evaluators, and academic supervisors
assessing the platform's design and functionality.

---

### 2. Overall Description

#### 2.1 User Roles
| Role | Capabilities |
|---|---|
| Student | Browse/enroll in courses, complete lessons & quizzes, track progress, chat with AI assistant, earn certificates |
| Instructor | Create/edit/publish courses, modules, lessons, and quizzes; view enrollment data |
| Admin | Manage users and roles, oversee platform-wide content and analytics |

#### 2.2 Product Perspective
A three-tier web application: a React single-page frontend, a Spring
Boot REST API backend, and a MariaDB (MySQL-compatible) relational database, with a
pluggable AI service layer.

---

### 3. Functional Requirements

**FR-1 Authentication** — Users register and log in; passwords are
hashed (BCrypt) and sessions are stateless JWTs.

**FR-2 Course Management** — Instructors create courses composed of
modules and ordered lessons (text + optional video).

**FR-3 Enrollment** — Students enroll in published courses; the system
prevents duplicate enrollment and tracks status (active/completed/dropped).

**FR-4 Assessment** — Instructors (or the AI assessment service) create
quizzes with multiple-choice questions; students submit attempts and
receive an instant score against a configurable passing threshold.

**FR-5 Progress Tracking** — The system records completion percentage
per student per course and the last lesson visited.

**FR-6 AI Assistant** — Students can ask course-related questions
(chatbot), receive course recommendations, and get an AI-generated,
adaptive study plan.

**FR-7 Certificates** — On course completion, a certificate record
(with a unique code) is issued to the student.

**FR-8 Admin Controls** — Admins can list users, change roles, and
deactivate accounts.

---

### 4. Non-Functional Requirements

- **Security**: JWT-based auth, role-based access control (`@PreAuthorize`),
  BCrypt password hashing, CORS restricted to known origins.
- **Scalability**: Stateless backend so multiple instances can run
  behind a load balancer; AI calls are isolated behind a service layer.
- **Maintainability**: Clear separation of controller / service /
  repository / model layers; DTOs decouple the API contract from entities.
- **Availability**: Health check endpoint (`/actuator/health`) for
  uptime monitoring.
- **Usability**: Responsive React UI; role-aware navigation.

---

### 5. External Interfaces

- **REST API** — JSON over HTTP, documented in `docs/API.md`.
- **AI Provider** — OpenAI-compatible `/chat/completions` endpoint,
  configurable via environment variables (see `.env.example`).
- **Database** — MariaDB (MySQL-compatible), schema in `database/schema/`.
