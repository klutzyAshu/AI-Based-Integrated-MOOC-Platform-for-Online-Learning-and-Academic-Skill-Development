# PROJECT AUDIT REPORT
**Project Name:** AI-Based Integrated MOOC Platform for Online Learning and Academic Skill Development  
**Auditor:** Senior Java Full-Stack Developer & Software Architect  
**Date:** November 16, 2026  
**Environment:** Arch Linux x86_64 | Java 27 OpenJDK / Java 25 & 21 available | Maven 3.10.1 | Node.js v20+ | MariaDB 13.0.2  

> **Status update — November 18, 2026:** This report is a historical snapshot; several findings below have since been addressed. The backend no longer uses Lombok and packages successfully. The frontend lockfile and ESLint configuration are present, and frontend lint and production builds pass. Learners can open lessons, take quizzes, track lesson completion and resume position, and receive a printable certificate after completing every lesson. Instructors can create, reorder, edit, and delete course modules and lessons. Administrators can manage categories. Admin and instructor analytics are available. Quiz answer keys are excluded from API JSON, answer indices are checked against each question's actual option count, and administrators cannot demote or deactivate their own account. Broader reporting remains limited. Docker access was unavailable during this update, so database-backed startup and end-to-end behavior remain unverified.

---

## 1. Executive Summary

A comprehensive architectural and code-level audit was conducted on the project repository located at `/home/ashu/Documents/mooc`. The platform is designed as an integrated Massive Open Online Course (MOOC) ecosystem comprising:
1. **Backend:** Java 17 / Spring Boot 3.3.2 REST API with Spring Data JPA, Spring Security (stateless JWT), and an OpenAI-compatible AI gateway.
2. **Frontend:** React 18 single-page application built with Vite 5, React Router v6, Axios, and custom CSS styling.
3. **Database:** MariaDB (relational schema covering users, courses, modules, lessons, enrollments, quizzes, questions, attempts, progress, certificates, and AI logs).
4. **AI Layer:** Multi-mode LLM abstraction supporting Chatbot, Course Recommendation, Quiz Assessment generation, and Adaptive Study Plan personalization, complemented by standalone Python reference implementations.

### Key Audit Finding
The project has a sound architectural foundation, clean package separation, and well-designed domain entities. However, **the project is currently non-compilable and non-executable out of the box** due to:
- A fatal Lombok compiler crash on modern OpenJDK versions (`java.lang.NoSuchFieldException: com.sun.tools.javac.code.TypeTag :: UNKNOWN`).
- Two zero-byte (empty) Java files causing packaging anomalies.
- Uninstalled frontend dependencies (`node_modules` missing).
- Inactive MariaDB daemon and unseeded database.
- Missing Maven wrapper (`mvnw`) and absence of `mvn` in standard system `$PATH`.

---

## 2. Completed Features

The following features have complete or near-complete implementations in the codebase:

1. **Authentication & Authorization Subsystem:**
   - Stateless JWT authentication (`JwtService`, `JwtAuthenticationFilter`).
   - Secure BCrypt password hashing (`PasswordEncoder`).
   - Role-based access control with three roles: `STUDENT`, `INSTRUCTOR`, `ADMIN`.
   - Registration (`/api/auth/register`) strictly restricted to creating `STUDENT` accounts by default.
   - Login (`/api/auth/login`) with credential validation and token issue.
   - User profile endpoints (`/api/users/me` with name update capability).
   - Administrative user governance (`/api/users`, `/api/users/{id}`, `/api/users/{id}/role`, and soft deactivation `/api/users/{id}`).

2. **Course Catalog & Public Discovery:**
   - Public published course retrieval (`GET /api/courses/public`).
   - Keyword search across published courses (`GET /api/courses/public/search?q=...`).
   - Detailed course retrieval (`GET /api/courses/{id}`) with permission guards ensuring unpublished courses are only accessible to authoring instructors or administrators.

3. **Course Authoring & Management:**
   - Instructor/Admin course creation (`POST /api/courses`).
   - Course updating (`PUT /api/courses/{id}`) with instructor ownership validation.
   - Course publication toggle (`PATCH /api/courses/{id}/publish`).
   - Course deletion (`DELETE /api/courses/{id}`).
   - Instructor-specific course listing (`GET /api/courses/instructor/{instructorId}`).

4. **Curriculum Hierarchy (Modules & Lessons):**
   - Hierarchical data model: `Course` -> `CourseModule` -> `Lesson`.
   - Lesson management: creation with module assignment, ordering, content, and video URL; retrieval by module; updating; deletion (`/api/lessons/**`).

5. **Enrollment Workflow:**
   - Enrollment action (`POST /api/enrollments/{courseId}`) with validations (student role check, course published check, duplicate enrollment prevention, dropped status reactivation).
   - Student enrollment listing (`GET /api/enrollments/me`).
   - Instructor enrollment inspection per course (`GET /api/enrollments/course/{courseId}`).
   - Course completion marking (`PATCH /api/enrollments/{courseId}/complete`).
   - Course drop mechanism (`DELETE /api/enrollments/{courseId}`).

6. **Quiz & Assessment Engine:**
   - Lesson-linked quiz creation (`POST /api/quizzes`) with multi-choice question support and JSON-serialized options.
   - Automated grading logic (`QuizService.grade`) comparing submitted choice indices with answer keys.
   - Quiz attempt persistence (`QuizAttemptRepository`) storing score, pass/fail evaluation against passing thresholds, and JSON audit trail of chosen options.
   - Quiz discovery by lesson (`GET /api/quizzes/lesson/{lessonId}`).

7. **AI Layer Integration:**
   - Unified API gateway (`POST /api/ai/ask`) accepting `AIRequest` with polymorphic dispatch for `CHATBOT`, `RECOMMENDATION`, `ASSESSMENT`, and `PERSONALIZATION`.
   - Graceful offline fallback: returns structured placeholder responses when `app.ai.api-key` is unconfigured, preventing runtime crashes in dev/demo environments.
   - Standalone Python scripts under `ai/` for assessment generation (`generate_quiz.py`), academic tutoring chatbot (`chatbot.py`), adaptive study plans (`study_plan.py`), and recommendation heuristics (`recommend.py`).

8. **Frontend Core Architecture:**
   - React 18 single-page app bundled with Vite 5.
   - Role-based routing with `ProtectedRoute` redirecting unauthenticated or unauthorized users.
   - Centralized Axios client (`services/api.js`) featuring automatic JWT injection and automatic redirection on 401 Unauthorized responses.
   - Functional pages for Login, Register, Student Dashboard, Course Viewer, Instructor Dashboard, and Admin Dashboard.

9. **Database Schema & Containerization Design:**
   - Normalized relational schema in `database/schema/*.sql` covering all entities with foreign keys and indexes.
   - Seed data script with hashed credentials (`Password123`) for default accounts: `admin@mooc.dev`, `instructor@mooc.dev`, `student@mooc.dev`.
   - `docker-compose.yml` orchestrating MariaDB 11.4, Spring Boot backend, and Vite frontend.

---

## 3. Incomplete Features

1. **Certificate Generation & Issuance:**
   - **Status:** Database table `certificates` and entity `Certificate.java` exist, and storage directory `storage/certificates` is defined.
   - **Gap:** No `CertificateRepository`, `CertificateService`, or REST controller endpoints exist to issue, render, or download certificates when a student completes an enrolled course or achieves a passing grade.

2. **Student Progress Tracking:**
   - **Status:** Database table `student_progress` and entity `StudentProgress.java` exist.
   - **Gap:** No `StudentProgressRepository` or service/controller layer logic updates completion percentages or saves `last_lesson_id` as students progress through lessons.

3. **Category Management APIs:**
   - **Status:** Entity `Category.java` and database table `categories` exist; courses reference categories.
   - **Gap:** No CRUD repository or REST controller exists for administrators or instructors to list, create, or modify categories dynamically via the API.

4. **Direct Course Module Management:**
   - **Status:** Entity `CourseModule.java` and table `course_modules` exist; cascading works through `Course`.
   - **Gap:** No standalone `CourseModuleRepository` or REST endpoints exist to reorder modules, add modules independently of full course payload, or manage module titles.

5. **Frontend Course Lesson & Quiz Interaction:**
   - **Status:** `CourseView.jsx` renders module titles and lesson titles as list items and provides an AI question box.
   - **Gap:** No interactive lesson viewer component (reading lesson markdown content or embedding video URLs) and no interactive quiz taking / submission form in the frontend UI.

---

## 4. Missing Components

1. **Maven Wrapper Executables:**
   - `scripts/setup.sh` and typical development workflows assume `./mvnw` is present in `backend/`.
   - `backend/` lacks `mvnw`, `mvnw.cmd`, and `.mvn/wrapper/`.
   - System `$PATH` does not include `mvn` (Maven 3.10.1 is located at `/home/ashu/.config/Code/User/globalStorage/pleiades.java-extension-pack-jdk/maven/latest/bin/mvn`).

2. **Zero-Byte / Dead Configuration Files:**
   - `backend/src/main/java/com/mooc/config/DatabaseConfig.java` is empty (0 bytes).
   - `backend/src/main/java/com/mooc/security/SecurityConfig.java` is empty (0 bytes), conflicting with the active configuration in `backend/src/main/java/com/mooc/config/SecurityConfig.java`.

3. **Frontend Dependencies:**
   - `frontend/node_modules/` is not installed.
   - Package manager lockfile (`package-lock.json`) is not present in `frontend/`.

4. **Environment File (`.env`):**
   - `.env.example` is present, but no active `.env` file exists in the project root.

5. **Automated Unit & Integration Tests:**
   - `backend/src/test/java` is completely empty. No unit or integration tests exist to verify repository methods, security filters, or REST controllers.

6. **Standard Lifecycle Execution Scripts:**
   - Phase 4 requirement scripts (`start.sh`, `stop.sh`, `build.sh`) are missing.

---

## 5. Compilation Errors (Root Cause Analysis)

### Root Cause: OpenJDK 27 & Lombok Incompatibility
When building the backend with Maven:
```
[ERROR] COMPILATION ERROR : 
[ERROR] /home/ashu/Documents/mooc/backend/src/main/java/com/mooc/config/SecurityConfig.java:[32,44] variable userDetailsService not initialized in the default constructor
[ERROR] /home/ashu/Documents/mooc/backend/src/main/java/com/mooc/config/SecurityConfig.java:[33,43] variable jwtAuthFilter not initialized in the default constructor
[ERROR] /home/ashu/Documents/mooc/backend/src/main/java/com/mooc/security/CustomUserDetailsService.java:[30,18] cannot find symbol: method isActive()
[ERROR] /home/ashu/Documents/mooc/backend/src/main/java/com/mooc/security/CustomUserDetailsService.java:[53,69] cannot find symbol: method getRole()
... (> 100 compilation errors)
```

**Diagnostic Analysis:**
1. The Arch Linux system default Java runtime is **OpenJDK 27.0.1.0.3**.
2. The project's `backend/pom.xml` relies on `org.projectlombok:lombok:1.18.34`.
3. In JDK 23 and higher, OpenJDK eliminated/refactored internal compiler symbols (`com.sun.tools.javac.code.TypeTag::UNKNOWN`).
4. When `javac` triggers Lombok's annotation processor, Lombok crashes immediately with:
   ```
   java.lang.NoSuchFieldException: com.sun.tools.javac.code.TypeTag :: UNKNOWN
       at lombok.permit.Permit.getField(Permit.java:144)
       at lombok.javac.JavacTreeMaker$SchroedingerType.getFieldCached(JavacTreeMaker.java:171)
   ```
5. As a result, Lombok aborts annotation processing, and none of the getters, setters, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, or `@RequiredArgsConstructor` methods are generated into AST.
6. This breaks 50+ classes throughout models, DTOs, services, and controllers.

---

## 6. Runtime Errors & Latent Issues

1. **MariaDB Daemon Inactive:**
   - Command `mariadb -u root -e "SELECT 1;"` produces:
     `ERROR 2002 (HY000): Can't connect to local server through socket '/run/mysqld/mysqld.sock' (2)`
   - Starting Spring Boot without an active database daemon causes immediate connection failure (`HikariPool - Exception during pool initialization`).
2. **Container Engine Unavailability:**
   - Docker daemon (`/var/run/docker.sock`) is not running. Local execution must run directly on the host using native processes rather than Docker Compose.
3. **Database Not Created / Not Seeded:**
   - The target database `mooc_platform` does not exist yet.
4. **Vite Development Server Unrunnable:**
   - Running `npm run dev` in `frontend` fails immediately with `vite: command not found` until `npm install` is executed.

---

## 7. Dependency & Configuration Analysis

1. **Backend `pom.xml`:**
   - `spring-boot-starter-parent` is set to `3.3.2`.
   - `java.version` is set to `17`.
   - `mariadb-java-client` version is `3.4.1` (`runtime` scope).
   - Missing explicit `maven-compiler-plugin` configuration for annotation processors.
   - Lombok reliance represents a single point of failure on modern JDK versions (Java 21/25/27).
2. **Backend `application.properties`:**
   - Datasource URL: `jdbc:mariadb://localhost:3306/mooc_platform?createDatabaseIfNotExist=true`
   - DDL-auto: `update`
   - Hibernate dialect: `org.hibernate.dialect.MariaDBDialect`
   - Environment variable placeholders are properly configured (`${DB_URL:...}`, `${DB_USERNAME:...}`, `${DB_PASSWORD:...}`, `${JWT_SECRET:...}`, `${AI_API_KEY:...}`).
3. **Frontend `package.json`:**
   - Dependencies: `react@18.3.1`, `react-dom@18.3.1`, `react-router-dom@6.24.0`, `axios@1.7.2`.
   - DevDependencies: `vite@5.3.1`, `@vitejs/plugin-react@4.3.1`.
   - Clean, lightweight, and modern without bloat.

---

## 8. Database Connection & Schema Analysis

1. **Relational Consistency:**
   - The SQL schema files in `database/schema/` are well-structured with appropriate primary keys, foreign key constraints (`ON DELETE CASCADE` / `ON DELETE SET NULL`), and indexing on high-frequency columns (`users.role`, `courses.published`, `lessons.module_id`, `enrollments.user_id`).
2. **Authentication Compatibility:**
   - Sample seeds in `database/seed/sample_data.sql` use valid BCrypt hashes corresponding to `Password123`, compatible with `BCryptPasswordEncoder` in `SecurityConfig.java`.
3. **Connection Strategy:**
   - In Arch Linux, standard MariaDB installations configure socket authentication for root. To enable Spring Boot connection over TCP `localhost:3306` with user credentials, a dedicated user or password-enabled account must be configured, or a standalone user-space MariaDB instance must be initialized.

---

## 9. Frontend & Backend Integration Analysis

1. **Proxy & Port Alignment:**
   - Frontend defaults to port `5173`.
   - Backend defaults to port `8080`.
   - `vite.config.js` configures `/api` proxying to `http://localhost:8080`.
   - `services/api.js` connects via `/api` (or `VITE_API_BASE_URL`).
2. **CORS:**
   - `backend/src/main/java/com/mooc/config/SecurityConfig.java` permits origins matching `http://localhost:*` with credentials, ensuring seamless cross-origin requests.
3. **Public Endpoint AntMatch Alignment:**
   - In `SecurityConfig.java`:
     `requestMatchers("/api/courses/public/**").permitAll()`
     `CourseController.java` defines `@GetMapping("/public")`.
     In Spring Security 6 PathPatternParser, explicit coverage for `/api/courses/public` (without trailing slash) should be guaranteed so that unauthenticated visitors never receive 401/403 when browsing the catalog.
4. **Token Interception:**
   - Frontend interceptor automatically handles token attachment and session expiry (401 redirection).
   - Role matching between backend (`STUDENT`, `INSTRUCTOR`, `ADMIN`) and frontend route guards is 100% consistent.

---

## 10. Remediation Plan (Phase 2 - Phase 5)

| Step | Objective | Action |
|---|---|---|
| **1** | Resolve Compilation Errors | Replace fragile Lombok bytecode modifications with explicit, clean standard Java POJO methods (getters, setters, constructors, builders) across all entities and DTOs; replace `@RequiredArgsConstructor` with explicit constructors in services/controllers. Remove dead zero-byte files (`DatabaseConfig.java`, duplicate `SecurityConfig.java`). |
| **2** | Ensure Maven & JVM Portability | Configure `pom.xml` with explicit `maven-compiler-plugin` configuration. Provide local `mvnw` wrapper or environment script pointing to Maven 3.10.1. |
| **3** | Database Initialization | Set up user-space or system MariaDB instance, create `mooc_platform` database, execute schema migrations, and load seed data with test accounts. |
| **4** | Frontend Preparation | Execute `npm install` in `frontend/` to populate `node_modules` and ensure Vite build passes. |
| **5** | Build & Verification | Execute full Maven compile, package, and test suite. Launch backend and verify `/actuator/health` and REST endpoints. Launch frontend and verify end-to-end integration. |
| **6** | Operational Scripts | Create `start.sh`, `stop.sh`, and `build.sh` for easy one-command execution on Arch Linux. |
| **7** | Final Documentation | Produce comprehensive `INSTALLATION.md`, API documentation, database setup guide, and updated `README.md`. |
