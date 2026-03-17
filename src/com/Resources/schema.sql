CREATE DATABASE IF NOT EXISTS fms_db;

USE fms_db;

CREATE TABLE IF NOT EXISTS Users (
                                     user_id INT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Lecturer', 'Student', 'Technical Officer') NOT NULL
    );
