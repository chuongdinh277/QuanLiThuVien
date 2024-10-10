package User;

import Database.DatabaseConnection;
import Document.Book;
import Document.Transaction;
import Document.TransactionDAO;
import Review.ReviewDAO;
import Review.Reviews;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private int id;
    private String userName;
    private String password;
    private String role;
    public User(String userName, String password,String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
    public String getUserName() {
        return userName;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Nếu có ít nhất 1 tài khoản với tên đăng nhập này
            }
        }
        return false;
    }

    private boolean registerUser(String userName, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.setString(3, role);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public void register() throws SQLException {
        try {
            boolean check = registerUser(this.getUserName(), this.getPassword(), this.getRole());
            if (check) {
                this.showAlberDialog(this.getUserName() + " " + "Đăng ký thành công");
            } else {
                this.showAlberDialog("Đăng ký thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }
    private boolean updatePassword(User user,String newPassword) throws SQLException {
        System.out.println(user.getPassword() + " " + newPassword);
        if(user.getPassword().equals(newPassword)) {
            return false;
        }
        String sql = "UPDATE users SET userName = ?,password = ? WHERE userName = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,user.getUserName());
            statement.setString(2, newPassword);
            statement.setString(3, user.getUserName());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0)  {
                user.setPassword(newPassword);
                System.out.println(user.getPassword());
            }
            return rowsUpdated > 0;
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return false;
    }
    public void updatePassword(String newPassword) {
        try {
            boolean check =  updatePassword(this, newPassword);
            if (check) {
                this.showAlberDialog("Cập nhật mật khẩu thành công");
            } else {
                this.showAlberDialog("Cập nhật mật khẩu thất bại");
            }
        } catch (SQLException e) {
            this.showErrorDialog("Database connection error", e.getMessage());
        }
    }
    public void deleteReview(String title, String author, String comment) throws SQLException {
        try {
            boolean check = ReviewDAO.removeReview(this.getUserName(), title, author, comment);
            if (check) {
                this.showAlberDialog("Tự xóa đánh giá thành công");
            } else {
                this.showAlberDialog("Tự xóa đánh giá thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error",e.getMessage());
        }
    }
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(new User(resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("role")));
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return users;
    }

    private String login(String username, String password) throws SQLException {
        String checkUsernameSQL = "SELECT * FROM users WHERE username = ?";
        String checkPasswordSQL = "SELECT * FROM users WHERE username = ? AND password = ? ";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Kiểm tra xem username có tồn tại không
            try (PreparedStatement checkUsernameStmt = connection.prepareStatement(checkUsernameSQL)) {
                checkUsernameStmt.setString(1, username);
                ResultSet usernameResult = checkUsernameStmt.executeQuery();
                if (!usernameResult.next()) {
                    return "Username not found"; // Tên đăng nhập không đúng
                }
            }

            // Kiểm tra xem mật khẩu có đúng không
            try (PreparedStatement checkPasswordStmt = connection.prepareStatement(checkPasswordSQL)) {
                checkPasswordStmt.setString(1, username);
                checkPasswordStmt.setString(2, password);
                ResultSet passwordResult = checkPasswordStmt.executeQuery();
                if (passwordResult.next()) {
                    return "Login successful"; // Đăng nhập thành công
                } else {
                    return "Incorrect password"; // Mật khẩu sai
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }


    public void login() {
        try {
            String isLogin = this.login(this.getUserName(), this.getPassword());
            if (isLogin.equals("Login successful")) {
                this.showAlberDialog(this.getUserName() + " " + "Đăng nhập thành công");
            } else {
                this.showAlberDialog(isLogin);
            }
        } catch (SQLException e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    public void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showAlberDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
    public List<Reviews> getAllReviewsByBook(Book book) {
        try {
            List<Reviews> reviews = ReviewDAO.getAllReviewsByBook(book);
            if (!reviews.isEmpty()) {
                this.showAlberDialog("Tìm thấy đánh giá sách");
                return reviews;
            } else {
                this.showAlberDialog("Không tìm thấy đánh giá nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    // khu vuc sua doi dung Book co link anh


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
