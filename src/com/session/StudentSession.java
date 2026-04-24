package com.session;

public class StudentSession {
    private static String username;

    private StudentSession() {
    }

    public static void setUsername(String username) {
        StudentSession.username = username;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        username = null;
    }

    public static boolean isLoggedIn() {
        return username != null && !username.isBlank();
    }
}
