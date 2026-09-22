-- ============================================================
-- Sample data for local development / demos
-- Password for all seeded users is: Password123  (BCrypt hash below)
-- ============================================================

INSERT INTO users (name, email, password, role) VALUES
('Ashu Admin', 'admin@mooc.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
('Ivy Instructor', 'instructor@mooc.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'INSTRUCTOR'),
('Sam Student', 'student@mooc.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT');

INSERT INTO categories (name, description) VALUES
('Computer Science', 'Programming, algorithms, and software engineering'),
('Data Science', 'Statistics, machine learning, and data analysis'),
('Mathematics', 'Core math skills for academic and technical study');

INSERT INTO courses (title, description, instructor_id, category_id, level, published) VALUES
('Introduction to Java', 'Learn Java fundamentals: syntax, OOP, and collections.',
 (SELECT id FROM users WHERE email = 'instructor@mooc.dev'),
 (SELECT id FROM categories WHERE name = 'Computer Science'), 'BEGINNER', TRUE),
('Data Structures & Algorithms', 'Master arrays, lists, trees, graphs, and complexity analysis.',
 (SELECT id FROM users WHERE email = 'instructor@mooc.dev'),
 (SELECT id FROM categories WHERE name = 'Computer Science'), 'INTERMEDIATE', TRUE);

INSERT INTO course_modules (course_id, title, module_order) VALUES
((SELECT id FROM courses WHERE title = 'Introduction to Java'), 'Getting Started', 1),
((SELECT id FROM courses WHERE title = 'Introduction to Java'), 'Object-Oriented Programming', 2);

INSERT INTO lessons (module_id, title, content, lesson_order, duration_minutes) VALUES
((SELECT id FROM course_modules WHERE title = 'Getting Started'),
 'Installing the JDK', 'Step-by-step guide to installing Java on your machine.', 1, 10),
((SELECT id FROM course_modules WHERE title = 'Getting Started'),
 'Your First Program', 'Write and run a Hello World program.', 2, 15),
((SELECT id FROM course_modules WHERE title = 'Object-Oriented Programming'),
 'Classes and Objects', 'Understand how to model real-world entities as classes.', 1, 20);

INSERT INTO enrollments (user_id, course_id, status) VALUES
((SELECT id FROM users WHERE email = 'student@mooc.dev'),
 (SELECT id FROM courses WHERE title = 'Introduction to Java'), 'ACTIVE');
