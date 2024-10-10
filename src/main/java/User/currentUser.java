package User;

public class currentUser {
    private static String username;
    private static String role;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        currentUser.username = username;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        currentUser.role = role;
    }
}
