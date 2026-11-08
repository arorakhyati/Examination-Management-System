-- Examination Management System
-- Run this file once in MySQL Workbench (File > Open SQL Script > Execute)

CREATE DATABASE IF NOT EXISTS examdb;
USE examdb;

DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

-- 1. users table (login)
CREATE TABLE users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(30) NOT NULL UNIQUE,
    password  VARCHAR(30) NOT NULL,
    role      VARCHAR(10) NOT NULL        -- Admin or User
);

-- 2. students table
CREATE TABLE students (
    student_id  INT AUTO_INCREMENT PRIMARY KEY,
    roll_no     VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(50) NOT NULL,
    course      VARCHAR(30) NOT NULL,
    semester    INT NOT NULL,
    phone       VARCHAR(10) NOT NULL
);

-- 3. exams table
CREATE TABLE exams (
    exam_id     INT AUTO_INCREMENT PRIMARY KEY,
    subject     VARCHAR(50) NOT NULL,
    semester    INT NOT NULL,
    exam_date   DATE NOT NULL,
    max_marks   INT NOT NULL
);

-- 4. results table (marks of a student in an exam)
CREATE TABLE results (
    result_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL,
    exam_id     INT NOT NULL,
    marks       INT NOT NULL,
    grade       VARCHAR(2) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    UNIQUE (student_id, exam_id)
);

-- default login users
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'Admin'),
('user',  'user123',  'User');

-- sample data
INSERT INTO students (roll_no, name, course, semester, phone) VALUES
('PIET25CS001', 'Aarav Sharma',  'B.Tech CSE', 3, '9876500001'),
('PIET25CS002', 'Riya Verma',    'B.Tech CSE', 3, '9876500002'),
('PIET25CS003', 'Mohit Jain',    'B.Tech CSE', 3, '9876500003'),
('PIET25CS004', 'Sneha Gupta',   'B.Tech CSE', 3, '9876500004'),
('PIET25IT005', 'Rohan Meena',   'B.Tech IT',  3, '9876500005'),
('PIET25IT006', 'Priya Singh',   'B.Tech IT',  3, '9876500006');

INSERT INTO exams (subject, semester, exam_date, max_marks) VALUES
('Data Structures',      3, '2026-12-05', 100),
('DBMS',                 3, '2026-12-07', 100),
('Discrete Mathematics', 3, '2026-12-09', 100),
('Java Programming',     3, '2026-12-11', 50);

INSERT INTO results (student_id, exam_id, marks, grade) VALUES
(1, 1, 82, 'A'),
(1, 2, 91, 'A+'),
(2, 1, 67, 'B'),
(2, 2, 74, 'B'),
(3, 1, 45, 'D'),
(3, 4, 38, 'A'),
(4, 3, 58, 'C'),
(5, 2, 33, 'F');
