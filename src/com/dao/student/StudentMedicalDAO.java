package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.MedicalSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentMedicalDAO {

    public List<MedicalSession> getAbsentSessions(String regNo) {
        List<MedicalSession> list = new ArrayList<>();

        String sql = """
                SELECT
                    ag.id AS attendance_group_id,
                    ag.course_id,
                    ag.session_id,
                    ag.type,
                    ag.attendance_date,
                    COALESCE(s.session_name, ag.session_id) AS session_name
                FROM attendance_record ar
                INNER JOIN attendance_group ag
                    ON ar.group_id = ag.id
                LEFT JOIN session s
                    ON ag.session_id = s.session_id
                    AND ag.course_id = s.course_id
                WHERE ar.reg_no = ?
                  AND ar.status = 'ABSENT'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM medical_selected_session mss
                      INNER JOIN medical m
                          ON m.medical_id = mss.medical_id
                      WHERE m.reg_no = ar.reg_no
                        AND mss.attendance_group_id = ag.id
                        AND m.status IN ('Pending', 'Approved')
                  )
                ORDER BY ag.attendance_date DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new MedicalSession(
                        rs.getInt("attendance_group_id"),
                        rs.getString("course_id"),
                        rs.getString("session_id"),
                        rs.getString("session_name"),
                        rs.getString("type"),
                        rs.getString("attendance_date")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}