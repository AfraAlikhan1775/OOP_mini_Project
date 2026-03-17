package com.database;

import java.sql.*;
import java.io.InputStream;
import java.util.Scanner;

public class DatabaseInitializer {

    // Define constants here - change once, updates everywhere
    private static final String URL = "jdbc:mysql://localhost:33061/fms_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Umes0820@@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        // Use the getConnection() method we just defined!
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream is = DatabaseInitializer.class.getResourceAsStream("/schema.sql");
            if (is == null) {
                System.err.println("Could not find schema.sql in Resources!");
                return;
            }

            try (Scanner scanner = new Scanner(is).useDelimiter(";")) {
                while (scanner.hasNext()) {
                    String query = scanner.next().trim();
                    if (!query.isEmpty()) {
                        stmt.execute(query);
                    }
                }
                System.out.println("Database initialized successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error during database initialization: " + e.getMessage());
        }
    }
}