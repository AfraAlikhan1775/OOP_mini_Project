-- ============================================================
-- FMS Database Schema — Faculty of Technology Management System
-- ============================================================

CREATE DATABASE IF NOT EXISTS fms_db;
USE fms_db;

-- ============================================================
-- USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id       INT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    role          ENUM('Admin', 'Lecturer', 'Student', 'Technical Officer') NOT NULL,
    profile_pic   VARCHAR(255)
);

INSERT IGNORE INTO users (username, password, role) VALUES
('admin', 'admin', 'Admin');

-- ============================================================
-- FACULTY TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS faculty (
    fac_id   VARCHAR(5) PRIMARY KEY,
    fac_name VARCHAR(30) NOT NULL,
    dean     VARCHAR(10)
);

INSERT IGNORE INTO faculty (fac_id, fac_name, dean) VALUES
('FAC07', 'Faculty of Technology', 'EMP0043');

-- ============================================================
-- DEPARTMENT TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS department (
    dep_id   VARCHAR(5) PRIMARY KEY,
    dep_name VARCHAR(50) NOT NULL,
    hod      VARCHAR(10) NOT NULL,
    fac_id   VARCHAR(5) NOT NULL,
    FOREIGN KEY (fac_id) REFERENCES faculty(fac_id) ON DELETE CASCADE
);

INSERT IGNORE INTO department (dep_id, dep_name, hod, fac_id) VALUES
('ICT', 'Information and Communication Technology', 'EMP0101', 'FAC07'),
('BST', 'Bio System Technology', 'EMP0110', 'FAC07'),
('ET',  'Engineering Technology', 'EMP0121', 'FAC07');

-- ============================================================
-- STUDENT TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS student (
    id                    INT PRIMARY KEY AUTO_INCREMENT,
    first_name            VARCHAR(100),
    last_name             VARCHAR(100),
    reg_no                VARCHAR(100) UNIQUE,
    nic                   VARCHAR(50),
    dob                   DATE,
    gender                VARCHAR(10),
    image_path            VARCHAR(255),
    district              VARCHAR(50),
    email                 VARCHAR(100),
    phone                 VARCHAR(20),
    address               TEXT,
    department            VARCHAR(50),
    course                VARCHAR(50),
    year_no               VARCHAR(10),
    mentor_id             VARCHAR(50),
    guardian_name          VARCHAR(100),
    guardian_phone         VARCHAR(20),
    guardian_relationship  VARCHAR(50)
);

-- ============================================================
-- LECTURER TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS lecturer (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    first_name     VARCHAR(100),
    last_name      VARCHAR(100),
    emp_id         VARCHAR(100) UNIQUE,
    nic            VARCHAR(50),
    dob            DATE,
    gender         VARCHAR(10),
    image_path     VARCHAR(255),
    district       VARCHAR(50),
    email          VARCHAR(100),
    phone          VARCHAR(20),
    address        TEXT,
    department     VARCHAR(50),
    specialization VARCHAR(100),
    designation    VARCHAR(100),
    qualification  VARCHAR(100)
);

-- ============================================================
-- TECHNICAL OFFICER TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS technical_officer (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    emp_id       VARCHAR(100) UNIQUE,
    nic          VARCHAR(50),
    dob          DATE,
    gender       VARCHAR(10),
    image_path   VARCHAR(255),
    district     VARCHAR(50),
    email        VARCHAR(100),
    phone        VARCHAR(20),
    address      TEXT,
    department   VARCHAR(50),
    position     VARCHAR(100),
    shift_type   VARCHAR(50),
    assigned_lab VARCHAR(100)
);

-- ============================================================
-- TIMETABLE GROUP TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS timetable_group (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    department VARCHAR(50) NOT NULL,
    level_no   INT NOT NULL,
    semester   INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TIMETABLE SESSION TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS timetable_session (
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    timetable_group_id INT NOT NULL,
    subject            VARCHAR(100) NOT NULL,
    day_name           VARCHAR(20) NOT NULL,
    start_time         VARCHAR(20) NOT NULL,
    end_time           VARCHAR(20) NOT NULL,
    lecturer           VARCHAR(100) NOT NULL,
    room               VARCHAR(50) NOT NULL,
    session_type       VARCHAR(50) NOT NULL,
    FOREIGN KEY (timetable_group_id) REFERENCES timetable_group(id) ON DELETE CASCADE
);

-- ============================================================
-- NOTICE TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS notice (
    notice_id    INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(200) NOT NULL,
    content      TEXT NOT NULL,
    category     ENUM('General', 'Academic', 'Exam', 'Event', 'Urgent') DEFAULT 'General',
    target_role  ENUM('All', 'Student', 'Lecturer', 'Technical Officer') DEFAULT 'All',
    posted_by    VARCHAR(50),
    posted_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- ATTENDANCE TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id   INT PRIMARY KEY AUTO_INCREMENT,
    student_id      VARCHAR(100) NOT NULL,
    course_code     VARCHAR(50) NOT NULL,
    attendance_date DATE NOT NULL,
    status          ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL,
    marked_by       VARCHAR(50),
    remarks         VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_attendance (student_id, course_code, attendance_date)
);

-- ============================================================
-- MEDICAL TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS medical (
    medical_id         INT PRIMARY KEY AUTO_INCREMENT,
    student_id         VARCHAR(100) NOT NULL,
    medical_data       LONGBLOB,
    medical_start_date DATE NOT NULL,
    medical_end_date   DATE NOT NULL,
    batch              VARCHAR(10),
    department         VARCHAR(50),
    reason             VARCHAR(255),
    added_by           VARCHAR(50),
    status             ENUM('Pending', 'Verified', 'Rejected') DEFAULT 'Pending',
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
