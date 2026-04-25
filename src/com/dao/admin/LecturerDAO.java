package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.Lecturer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    public LecturerDAO() {
        createTable();
        createDegreeTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lecturer (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    emp_id VARCHAR(100) UNIQUE NOT NULL,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    nic VARCHAR(50),
                    dob DATE,
                    gender VARCHAR(20),
                    reg_pic VARCHAR(255),
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
                    status VARCHAR(20)
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDegreeTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lecturer_degree (
                    degree_id INT PRIMARY KEY AUTO_INCREMENT,
                    emp_id VARCHAR(100) NOT NULL,
                    degree_name VARCHAR(255) NOT NULL,
                    CONSTRAINT fk_lecturer_degree_emp
                        FOREIGN KEY (emp_id) REFERENCES lecturer(emp_id)
                        ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsByEmpId(String empId) {
        String sql = "SELECT COUNT(*) FROM lecturer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean saveLecturer(Lecturer lecturer) {
        String sql = """
                INSERT INTO lecturer (
                    emp_id, first_name, last_name, nic, dob, gender, reg_pic,
                    contact_number, email, emergency_contact, district, address,
                    department, lecturer_type, appointment_date, specialization,
                    experience_years, status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, lecturer.getEmployeeId());
            pst.setString(2, lecturer.getFirstName());
            pst.setString(3, lecturer.getLastName());
            pst.setString(4, lecturer.getNic());

            if (lecturer.getDob() != null) {
                pst.setDate(5, Date.valueOf(lecturer.getDob()));
            } else {
                pst.setDate(5, null);
            }

            pst.setString(6, lecturer.getGender());
            pst.setString(7, lecturer.getRegPic());
            pst.setString(8, lecturer.getContactNumber());
            pst.setString(9, lecturer.getEmail());
            pst.setString(10, lecturer.getEmergencyContact());
            pst.setString(11, lecturer.getDistrict());
            pst.setString(12, lecturer.getAddress());
            pst.setString(13, lecturer.getDepartment());
            pst.setString(14, lecturer.getLecturerType());

            if (lecturer.getAppointmentDate() != null) {
                pst.setDate(15, Date.valueOf(lecturer.getAppointmentDate()));
            } else {
                pst.setDate(15, null);
            }

            pst.setString(16, lecturer.getSpecialization());
            pst.setInt(17, lecturer.getExperienceYears());
            pst.setString(18, lecturer.getStatus());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLecturer(Lecturer lecturer) {
        String sql = """
            UPDATE lecturer SET
                first_name = ?,
                last_name = ?,
                nic = ?,
                dob = ?,
                gender = ?,
                reg_pic = ?,
                contact_number = ?,
                email = ?,
                emergency_contact = ?,
                district = ?,
                address = ?,
                department = ?,
                lecturer_type = ?,
                appointment_date = ?,
                specialization = ?,
                experience_years = ?,
                status = ?
            WHERE emp_id = ?
            """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, lecturer.getFirstName());
            pst.setString(2, lecturer.getLastName());
            pst.setString(3, lecturer.getNic());

            if (lecturer.getDob() != null) {
                pst.setDate(4, Date.valueOf(lecturer.getDob()));
            } else {
                pst.setDate(4, null);
            }

            pst.setString(5, lecturer.getGender());
            pst.setString(6, lecturer.getRegPic());
            pst.setString(7, lecturer.getContactNumber());
            pst.setString(8, lecturer.getEmail());
            pst.setString(9, lecturer.getEmergencyContact());
            pst.setString(10, lecturer.getDistrict());
            pst.setString(11, lecturer.getAddress());
            pst.setString(12, lecturer.getDepartment());
            pst.setString(13, lecturer.getLecturerType());

            if (lecturer.getAppointmentDate() != null) {
                pst.setDate(14, Date.valueOf(lecturer.getAppointmentDate()));
            } else {
                pst.setDate(14, null);
            }

            pst.setString(15, lecturer.getSpecialization());
            pst.setInt(16, lecturer.getExperienceYears());
            pst.setString(17, lecturer.getStatus());
            pst.setString(18, lecturer.getEmployeeId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean saveLecturerDegree(String empId, String degreeName) {
        String sql = "INSERT INTO lecturer_degree (emp_id, degree_name) VALUES (?, ?)";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);
            pst.setString(2, degreeName);
            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLecturerDegrees(String empId) {
        String sql = "DELETE FROM lecturer_degree WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);
            return pst.executeUpdate() >= 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getLecturerDegrees(String empId) {
        List<String> degrees = new ArrayList<>();
        String sql = "SELECT degree_name FROM lecturer_degree WHERE emp_id = ? ORDER BY degree_id ASC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    degrees.add(rs.getString("degree_name"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return degrees;
    }

    public Lecturer getLecturerByEmpId(String empId) {
        String sql = "SELECT * FROM lecturer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractLecturer(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT * FROM lecturer ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lecturers.add(extractLecturer(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }

    public List<Lecturer> searchLecturers(String keyword) {
        List<Lecturer> lecturers = new ArrayList<>();

        String sql = """
                SELECT * FROM lecturer
                WHERE first_name LIKE ?
                   OR last_name LIKE ?
                   OR emp_id LIKE ?
                   OR department LIKE ?
                   OR specialization LIKE ?
                ORDER BY id DESC
                """;

        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);
            pst.setString(5, pattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lecturers.add(extractLecturer(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }

    public List<Lecturer> filterByDepartment(String department) {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT * FROM lecturer WHERE department = ? ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lecturers.add(extractLecturer(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }

    public boolean deleteLecturerByEmpId(String empId) {
        String sql = "DELETE FROM lecturer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Lecturer extractLecturer(ResultSet rs) throws Exception {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(rs.getInt("id"));
        lecturer.setFirstName(rs.getString("first_name"));
        lecturer.setLastName(rs.getString("last_name"));
        lecturer.setEmployeeId(rs.getString("emp_id"));
        lecturer.setNic(rs.getString("nic"));
        lecturer.setDob(rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null);
        lecturer.setGender(rs.getString("gender"));
        lecturer.setRegPic(rs.getString("reg_pic"));
        lecturer.setContactNumber(rs.getString("contact_number"));
        lecturer.setEmail(rs.getString("email"));
        lecturer.setEmergencyContact(rs.getString("emergency_contact"));
        lecturer.setDistrict(rs.getString("district"));
        lecturer.setAddress(rs.getString("address"));
        lecturer.setDepartment(rs.getString("department"));
        lecturer.setLecturerType(rs.getString("lecturer_type"));
        lecturer.setAppointmentDate(rs.getDate("appointment_date") != null ? rs.getDate("appointment_date").toLocalDate() : null);
        lecturer.setSpecialization(rs.getString("specialization"));
        lecturer.setExperienceYears(rs.getInt("experience_years"));
        lecturer.setStatus(rs.getString("status"));
        return lecturer;
    }
}