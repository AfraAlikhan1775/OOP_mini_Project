CREATE DATABASE IF NOT EXISTS fms_db;
USE fms_db;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS users (
                                     user_id INT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     role ENUM('Admin','Lecturer','Student','Technical Officer') NOT NULL,
                                     profile_pic VARCHAR(500),
                                     ref_id VARCHAR(100),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO users (username, password, role, profile_pic, ref_id)
VALUES ('admin', 'admin', 'Admin', NULL, 'admin');

CREATE TABLE IF NOT EXISTS faculty (
                                       fac_id VARCHAR(5) PRIMARY KEY,
                                       fac_name VARCHAR(50) NOT NULL,
                                       dean VARCHAR(50)
);

INSERT IGNORE INTO faculty (fac_id, fac_name, dean)
VALUES ('FAC07', 'Faculty of Technology', 'EMP0043');

CREATE TABLE IF NOT EXISTS department (
                                          dep_id VARCHAR(10) PRIMARY KEY,
                                          dep_name VARCHAR(100) NOT NULL,
                                          hod VARCHAR(50),
                                          fac_id VARCHAR(5),
                                          CONSTRAINT fk_department_faculty
                                              FOREIGN KEY (fac_id) REFERENCES faculty(fac_id)
                                                  ON UPDATE CASCADE
                                                  ON DELETE SET NULL
);

INSERT IGNORE INTO department (dep_id, dep_name, hod, fac_id) VALUES
                                                                  ('ICT', 'Information and Communication Technology', 'EMP0101', 'FAC07'),
                                                                  ('BST', 'Bio System Technology', 'EMP0110', 'FAC07'),
                                                                  ('ET',  'Engineering Technology', 'EMP0121', 'FAC07');

CREATE TABLE IF NOT EXISTS student (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       first_name VARCHAR(100),
                                       last_name VARCHAR(100),
                                       reg_no VARCHAR(100) UNIQUE,
                                       nic VARCHAR(50),
                                       dob DATE,
                                       gender VARCHAR(20),
                                       image_path VARCHAR(500),
                                       district VARCHAR(50),
                                       email VARCHAR(100),
                                       phone VARCHAR(20),
                                       address TEXT,
                                       department VARCHAR(50),
                                       degrea VARCHAR(50),
                                       year_no VARCHAR(10),
                                       mentor_id VARCHAR(50),
                                       guardian_name VARCHAR(100),
                                       guardian_phone VARCHAR(20),
                                       guardian_relationship VARCHAR(50),
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       INDEX idx_student_reg_no (reg_no),
                                       INDEX idx_student_department_year (department, year_no)
);

CREATE TABLE IF NOT EXISTS lecturer (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        emp_id VARCHAR(100) UNIQUE NOT NULL,
                                        first_name VARCHAR(100),
                                        last_name VARCHAR(100),
                                        nic VARCHAR(50),
                                        dob DATE,
                                        gender VARCHAR(20),
                                        reg_pic VARCHAR(500),
                                        contact_number VARCHAR(20),
                                        email VARCHAR(100),
                                        emergency_contact VARCHAR(20),
                                        district VARCHAR(50),
                                        address TEXT,
                                        department VARCHAR(50),
                                        lecturer_type VARCHAR(50),
                                        appointment_date DATE,
                                        specialization VARCHAR(100),
                                        experience_years INT DEFAULT 0,
                                        status VARCHAR(20),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        INDEX idx_lecturer_emp_id (emp_id),
                                        INDEX idx_lecturer_department (department)
);

CREATE TABLE IF NOT EXISTS lecturer_degree (
                                               degree_id INT PRIMARY KEY AUTO_INCREMENT,
                                               emp_id VARCHAR(100) NOT NULL,
                                               degree_name VARCHAR(255) NOT NULL,
                                               CONSTRAINT fk_lecturer_degree_emp
                                                   FOREIGN KEY (emp_id) REFERENCES lecturer(emp_id)
                                                       ON UPDATE CASCADE
                                                       ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS technical_officer (
                                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                                 first_name VARCHAR(100),
                                                 last_name VARCHAR(100),
                                                 emp_id VARCHAR(100) UNIQUE,
                                                 nic VARCHAR(50),
                                                 dob DATE,
                                                 gender VARCHAR(20),
                                                 image_path VARCHAR(500),
                                                 district VARCHAR(50),
                                                 email VARCHAR(100),
                                                 phone VARCHAR(20),
                                                 address TEXT,
                                                 department VARCHAR(50),
                                                 position VARCHAR(100),
                                                 shift_type VARCHAR(50),
                                                 assigned_lab VARCHAR(100),
                                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                 INDEX idx_to_emp_id (emp_id),
                                                 INDEX idx_to_department (department)
);

CREATE TABLE IF NOT EXISTS courses (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       department VARCHAR(50) NOT NULL,
                                       year VARCHAR(20) NOT NULL,
                                       semester VARCHAR(20) NOT NULL,
                                       course_id VARCHAR(50) NOT NULL UNIQUE,
                                       course_name VARCHAR(150) NOT NULL,
                                       coordinator VARCHAR(100) NOT NULL,
                                       credits INT NOT NULL DEFAULT 1,
                                       image_path VARCHAR(500),
                                       status VARCHAR(30) DEFAULT 'Active',
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       INDEX idx_courses_year_semester (year, semester),
                                       INDEX idx_courses_department (department),
                                       INDEX idx_courses_coordinator (coordinator)
);

CREATE TABLE IF NOT EXISTS session (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       session_id VARCHAR(100) NOT NULL,
                                       session_name VARCHAR(255),
                                       type VARCHAR(20) NOT NULL,
                                       course_id VARCHAR(100) NOT NULL,
                                       year_no INT NOT NULL,
                                       semester INT NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       UNIQUE KEY uq_session_course_type (session_id, course_id, type),
                                       INDEX idx_session_course (course_id),
                                       INDEX idx_session_year_semester (year_no, semester)
);

CREATE TABLE IF NOT EXISTS notices (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       title VARCHAR(200) NOT NULL,
                                       description TEXT NOT NULL,
                                       pdf_name VARCHAR(255) NOT NULL,
                                       pdf_data LONGBLOB NOT NULL,
                                       role_target VARCHAR(50) NOT NULL,
                                       batch_target VARCHAR(50) NOT NULL,
                                       department_target VARCHAR(50) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       INDEX idx_notices_target (role_target, batch_target, department_target),
                                       INDEX idx_notices_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS timetable_group (
                                               id INT PRIMARY KEY AUTO_INCREMENT,
                                               department VARCHAR(50) NOT NULL,
                                               level_no INT NOT NULL,
                                               semester INT NOT NULL,
                                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               INDEX idx_timetable_group_filter (department, level_no, semester)
);

CREATE TABLE IF NOT EXISTS timetable_session (
                                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                                 timetable_group_id INT NOT NULL,
                                                 subject VARCHAR(100) NOT NULL,
                                                 day_name VARCHAR(20) NOT NULL,
                                                 start_time VARCHAR(20) NOT NULL,
                                                 end_time VARCHAR(20) NOT NULL,
                                                 lecturer VARCHAR(100) NOT NULL,
                                                 room VARCHAR(50) NOT NULL,
                                                 session_type VARCHAR(50) NOT NULL,
                                                 CONSTRAINT fk_timetable_session_group
                                                     FOREIGN KEY (timetable_group_id) REFERENCES timetable_group(id)
                                                         ON UPDATE CASCADE
                                                         ON DELETE CASCADE,
                                                 INDEX idx_timetable_session_group (timetable_group_id),
                                                 INDEX idx_timetable_lecturer (lecturer)
);

CREATE TABLE IF NOT EXISTS attendance_group (
                                                id INT PRIMARY KEY AUTO_INCREMENT,
                                                year_no INT NOT NULL,
                                                course_id VARCHAR(100) NOT NULL,
                                                session_id VARCHAR(100) NOT NULL,
                                                type VARCHAR(20) NOT NULL,
                                                attendance_date DATE NOT NULL,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                INDEX idx_att_group_filter (year_no, course_id, session_id, type),
                                                INDEX idx_att_group_date (attendance_date)
);

CREATE TABLE IF NOT EXISTS attendance_record (
                                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                                 group_id INT NOT NULL,
                                                 attendance_group_id INT NULL,
                                                 reg_no VARCHAR(100) NOT NULL,
                                                 status VARCHAR(20) NOT NULL,
                                                 medical_status VARCHAR(20) DEFAULT NULL,
                                                 CONSTRAINT fk_attendance_record_group
                                                     FOREIGN KEY (group_id) REFERENCES attendance_group(id)
                                                         ON UPDATE CASCADE
                                                         ON DELETE CASCADE,
                                                 INDEX idx_att_record_reg_no (reg_no),
                                                 INDEX idx_att_record_group (group_id),
                                                 INDEX idx_att_record_status (status, medical_status)
);

CREATE TABLE IF NOT EXISTS medical (
                                       medical_id INT AUTO_INCREMENT PRIMARY KEY,
                                       reg_no VARCHAR(50),
                                       student_id VARCHAR(100),
                                       file_path VARCHAR(500),
                                       medical_data LONGBLOB,
                                       start_date DATE,
                                       end_date DATE,
                                       medical_start_date DATE,
                                       medical_end_date DATE,
                                       batch VARCHAR(10),
                                       department VARCHAR(50),
                                       reason TEXT,
                                       added_by VARCHAR(50),
                                       status VARCHAR(20) DEFAULT 'Pending',
                                       approved_by VARCHAR(50),
                                       approved_at TIMESTAMP NULL,
                                       reject_reason TEXT,
                                       submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       INDEX idx_medical_reg_no (reg_no),
                                       INDEX idx_medical_status (status),
                                       INDEX idx_medical_dates (medical_start_date, medical_end_date)
);

CREATE TABLE IF NOT EXISTS medical_selected_session (
                                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                                        medical_id INT NOT NULL,
                                                        attendance_group_id INT NULL,
                                                        course_id VARCHAR(100),
                                                        session_id VARCHAR(100),
                                                        session_name VARCHAR(150),
                                                        type VARCHAR(30),
                                                        attendance_date DATE,
                                                        medical_for VARCHAR(20) DEFAULT 'ATTENDANCE',
                                                        exam_type VARCHAR(50) NULL,
                                                        exam_date DATE NULL,
                                                        status VARCHAR(20) DEFAULT 'Pending',
                                                        CONSTRAINT fk_medical_selected_medical
                                                            FOREIGN KEY (medical_id) REFERENCES medical(medical_id)
                                                                ON UPDATE CASCADE
                                                                ON DELETE CASCADE,
                                                        INDEX idx_mss_medical_id (medical_id),
                                                        INDEX idx_mss_course_exam (course_id, medical_for, exam_type, exam_date),
                                                        INDEX idx_mss_attendance_group (attendance_group_id),
                                                        INDEX idx_mss_status (status)
);

CREATE TABLE IF NOT EXISTS course_registration (
                                                   reg_no VARCHAR(50) NOT NULL,
                                                   course_id VARCHAR(50) NOT NULL,
                                                   semester VARCHAR(20) NOT NULL,
                                                   academic_year VARCHAR(20) NOT NULL,
                                                   registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                   PRIMARY KEY (reg_no, course_id, semester, academic_year),
                                                   CONSTRAINT fk_course_registration_student
                                                       FOREIGN KEY (reg_no) REFERENCES student(reg_no)
                                                           ON UPDATE CASCADE
                                                           ON DELETE CASCADE,
                                                   CONSTRAINT fk_course_registration_course
                                                       FOREIGN KEY (course_id) REFERENCES courses(course_id)
                                                           ON UPDATE CASCADE
                                                           ON DELETE CASCADE,
                                                   INDEX idx_course_registration_course (course_id),
                                                   INDEX idx_course_registration_reg_no (reg_no)
);

CREATE TABLE IF NOT EXISTS marks_group (
                                           group_id INT AUTO_INCREMENT PRIMARY KEY,
                                           course_id VARCHAR(50) NOT NULL,
                                           year VARCHAR(20) NOT NULL,
                                           semester VARCHAR(20) NOT NULL,
                                           academic_year VARCHAR(20) NOT NULL,
                                           exam_type VARCHAR(50) NOT NULL,
                                           exam_date DATE NULL,
                                           created_by VARCHAR(50) NOT NULL,
                                           UNIQUE KEY uq_marks_group (course_id, year, semester, academic_year, exam_type, created_by),
                                           INDEX idx_marks_group_course (course_id),
                                           INDEX idx_marks_group_creator (created_by),
                                           INDEX idx_marks_group_exam (exam_type, exam_date)
);

CREATE TABLE IF NOT EXISTS student_mark_entry (
                                                  group_id INT NOT NULL,
                                                  reg_no VARCHAR(50) NOT NULL,
                                                  raw_mark DOUBLE NOT NULL,
                                                  PRIMARY KEY (group_id, reg_no),
                                                  CONSTRAINT fk_student_mark_entry_group
                                                      FOREIGN KEY (group_id) REFERENCES marks_group(group_id)
                                                          ON UPDATE CASCADE
                                                          ON DELETE CASCADE,
                                                  INDEX idx_mark_entry_reg_no (reg_no)
);

CREATE TABLE IF NOT EXISTS course_assessment_scheme (
                                                        course_id VARCHAR(50) PRIMARY KEY,
                                                        has_theory BOOLEAN NOT NULL DEFAULT TRUE,
                                                        has_practical BOOLEAN NOT NULL DEFAULT FALSE,
                                                        CONSTRAINT fk_assessment_scheme_course
                                                            FOREIGN KEY (course_id) REFERENCES courses(course_id)
                                                                ON UPDATE CASCADE
                                                                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_marks (
                                             course_id VARCHAR(50) NOT NULL,
                                             reg_no VARCHAR(50) NOT NULL,
                                             quiz1 DECIMAL(5,2) DEFAULT 0,
                                             quiz2 DECIMAL(5,2) DEFAULT 0,
                                             quiz3 DECIMAL(5,2) DEFAULT 0,
                                             assignment_mark DECIMAL(5,2) DEFAULT 0,
                                             mid_exam DECIMAL(5,2) DEFAULT 0,
                                             final_theory DECIMAL(5,2) DEFAULT 0,
                                             final_practical DECIMAL(5,2) DEFAULT 0,
                                             ca_mark DECIMAL(6,2) DEFAULT 0,
                                             final_mark DECIMAL(6,2) DEFAULT 0,
                                             PRIMARY KEY (course_id, reg_no),
                                             CONSTRAINT fk_student_marks_course
                                                 FOREIGN KEY (course_id) REFERENCES courses(course_id)
                                                     ON UPDATE CASCADE
                                                     ON DELETE CASCADE,
                                             CONSTRAINT fk_student_marks_student
                                                 FOREIGN KEY (reg_no) REFERENCES student(reg_no)
                                                     ON UPDATE CASCADE
                                                     ON DELETE CASCADE,
                                             INDEX idx_student_marks_reg_no (reg_no)
);

CREATE TABLE IF NOT EXISTS course_materials (
                                                id INT PRIMARY KEY AUTO_INCREMENT,
                                                course_id VARCHAR(50) NOT NULL,
                                                week_no INT NOT NULL,
                                                title VARCHAR(150) NOT NULL,
                                                file_name VARCHAR(255) NOT NULL,
                                                pdf_file LONGBLOB NOT NULL,
                                                uploaded_by VARCHAR(100) NOT NULL,
                                                uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                INDEX idx_course_materials_course_week (course_id, week_no),
                                                INDEX idx_course_materials_uploaded_by (uploaded_by)
);

CREATE TABLE IF NOT EXISTS course_announcements (
                                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                                    course_id VARCHAR(50) NOT NULL,
                                                    week_no INT NOT NULL,
                                                    announcement_text TEXT NOT NULL,
                                                    created_by VARCHAR(100) NOT NULL,
                                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                    INDEX idx_course_announcements_course_week (course_id, week_no),
                                                    INDEX idx_course_announcements_created_by (created_by)
);

CREATE TABLE IF NOT EXISTS course_notes (
                                            id INT PRIMARY KEY AUTO_INCREMENT,
                                            course_id VARCHAR(50) NOT NULL,
                                            lecturer_emp_id VARCHAR(100) NOT NULL,
                                            title VARCHAR(150) NOT NULL,
                                            note_text TEXT NOT NULL,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            INDEX idx_course_notes_course (course_id),
                                            INDEX idx_course_notes_lecturer (lecturer_emp_id)
);

SET FOREIGN_KEY_CHECKS = 1;
