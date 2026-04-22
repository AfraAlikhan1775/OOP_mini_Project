package com.database;

import java.sql.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseInitializer {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();

        try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }

        URL = resolve(props, "db.url", "DB_URL", "jdbc:mysql://localhost:33061/fms_db");
        USER = resolve(props, "db.user", "DB_USER", "root");
        PASSWORD = resolve(props, "db.password", "DB_PASSWORD", "Umes0820@@");
    }

    private static String resolve(Properties props, String propKey, String envKey, String fallback) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) return env;
        String sys = System.getProperty(propKey);
        if (sys != null && !sys.isBlank()) return sys;
        return props.getProperty(propKey, fallback);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream is = DatabaseInitializer.class.getResourceAsStream("/com/Resources/schema.sql");
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
