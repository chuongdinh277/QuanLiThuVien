package User;

public class currentUser {
    private static int id;
    private static String username;
    private static String role;
    private static String password;
    private static String email;
    private static String fullName;
    private static String number;

    public static String getEmail() {
        return email;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getNumber() {
        return number;
    }

    public static int getId() {
        return id;
    }

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

    public static void setId(int id) {
        currentUser.id = id;
    }

    public static void setEmail(String email) {
        currentUser.email = email;
    }

    public static void setFullName(String fullName) {
        currentUser.fullName = fullName;
    }

    public static void setNumber(String number) {
        currentUser.number = number;
    }

    public static void logout () {
        currentUser.setUsername(null);
        currentUser.setRole(null);
        currentUser.setPassword(null);
    }
}