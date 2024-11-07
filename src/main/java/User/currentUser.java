package User;

public class currentUser {
    private static String username;
    private static String role;
    private static String password;
    private static int id;
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
    public static String getPassword() {
        return password;
    }
    public static void setPassword(String password) {
        currentUser.password = password;
    }
    public static int getID() {
        return id;
    }
    public static void setID(int id) {
        currentUser.id = id;
    }
    public static void logout () {
        currentUser.setUsername(null);
        currentUser.setRole(null);
        currentUser.setPassword(null);
    }
}
