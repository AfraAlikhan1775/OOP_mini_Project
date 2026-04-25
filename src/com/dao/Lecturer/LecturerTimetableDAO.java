package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.admin.TimetableSessionInput;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerTimetableDAO {

    public List<TimetableSessionInput> getLecturerTimetable(String lecturerEmpId, String dayFilter) {
        List<TimetableSessionInput> list = new ArrayList<>();

        LecturerName lecturerName = getLecturerName(lecturerEmpId);

        String sql = """
                SELECT
                    ts.subject,
                    ts.day_name,
                    ts.start_time,
                    ts.end_time,
                    ts.lecturer,
                    ts.room,
                    ts.session_type
                FROM timetable_session ts
                INNER JOIN timetable_group tg
                    ON ts.timetable_group_id = tg.id
                WHERE (
                    LOWER(TRIM(ts.lecturer)) = LOWER(TRIM(?))
                    OR LOWER(TRIM(ts.lecturer)) = LOWER(TRIM(?))
                    OR LOWER(TRIM(ts.lecturer)) = LOWER(TRIM(?))
                    OR LOWER(TRIM(ts.lecturer)) LIKE LOWER(TRIM(?))
                )
                """;

        if (dayFilter != null && !dayFilter.equalsIgnoreCase("All")) {
            sql += " AND ts.day_name = ? ";
        }

        sql += """
                ORDER BY
                    CASE ts.day_name
                        WHEN 'Monday' THEN 1
                        WHEN 'Tuesday' THEN 2
                        WHEN 'Wednesday' THEN 3
                        WHEN 'Thursday' THEN 4
                        WHEN 'Friday' THEN 5
                        ELSE 6
                    END,
                    ts.start_time
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int index = 1;

            ps.setString(index++, lecturerEmpId);
            ps.setString(index++, lecturerName.fullName);
            ps.setString(index++, lecturerName.shortName);
            ps.setString(index++, "%" + lecturerEmpId + "%");

            if (dayFilter != null && !dayFilter.equalsIgnoreCase("All")) {
                ps.setString(index, dayFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableSessionInput(
                            rs.getString("subject"),
                            rs.getString("day_name"),
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getString("lecturer"),
                            rs.getString("room"),
                            rs.getString("session_type")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private LecturerName getLecturerName(String lecturerEmpId) {
        String sql = """
                SELECT first_name, last_name
                FROM lecturer
                WHERE emp_id = ?
                LIMIT 1
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lecturerEmpId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String firstName = safe(rs.getString("first_name"));
                    String lastName = safe(rs.getString("last_name"));

                    return new LecturerName(
                            (firstName + " " + lastName).trim(),
                            firstName
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new LecturerName(lecturerEmpId, lecturerEmpId);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static class LecturerName {
        String fullName;
        String shortName;

        LecturerName(String fullName, String shortName) {
            this.fullName = fullName;
            this.shortName = shortName;
        }
    }
}