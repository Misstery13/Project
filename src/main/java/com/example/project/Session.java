package com.example.project;

public final class Session {
    public enum Role {
        ADMINISTRADOR,
        BODEGUERO
    }

    private static String username;
    private static Role role;

    private Session() {}

    public static void start(String user, Role selectedRole) {
        username = user;
        role = selectedRole;
    }

    public static String getUsername() {
        return username;
    }

    public static Role getRole() {
        return role;
    }

    public static boolean isActive() {
        return username != null && role != null;
    }

    public static void clear() {
        username = null;
        role = null;
    }
}


