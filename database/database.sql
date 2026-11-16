-- ============================================================
-- Master schema file for the AI-Based Integrated MOOC Platform.
-- Targets MariaDB (also runs fine on MySQL).
-- Run this to create the database and all tables in the right order.
-- Usage:  mariadb -u root -p < database.sql
--    or:  mysql -u root -p < database.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS mooc_platform CHARACTER SET utf8mb4;
USE mooc_platform;

-- Uncomment to load sample data for local development:
-- Apply files in database/schema/ in dependency order, then optionally
-- database/seed/sample_data.sql. Docker Compose mounts each schema file
-- as an ordered MariaDB initialization script.
