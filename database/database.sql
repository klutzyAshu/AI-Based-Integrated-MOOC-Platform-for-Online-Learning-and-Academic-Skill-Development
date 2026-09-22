-- ============================================================
-- Master schema file for the AI-Based Integrated MOOC Platform.
-- Targets MariaDB (also runs fine on MySQL).
-- Run this to create the database and all tables in the right order.
-- Usage:  mariadb -u root -p < database.sql
--    or:  mysql -u root -p < database.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS mooc_platform CHARACTER SET utf8mb4;
USE mooc_platform;

SOURCE schema/users.sql;
SOURCE schema/courses.sql;
SOURCE schema/enrollments.sql;
SOURCE schema/quizzes.sql;
SOURCE schema/ai.sql;

-- Uncomment to load sample data for local development:
-- SOURCE seed/sample_data.sql;
