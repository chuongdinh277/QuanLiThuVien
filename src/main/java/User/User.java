package User;

import Database.DatabaseConnection;
import Document.Book;
import Review.ReviewDAO;
import Review.Reviews;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;

public class User {
    private int id;
    private String userName;
    private String password;
    private String role;
    private String fullName;
    private String email;
    private String number;

    /**
     * create a new user based on the constructor parameters.
     * @param id the id of the new user.
     * @param userName the name of the new user.
     * @param password the password of the new user.
     * @param role the role of the new user.
     * @param fullName the full name of the new user.
     * @param email the email of the new user.
     * @param number the number of the new user.
     */
    public User(int id, String userName, String password, String role, String fullName, String email, String number) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.number = number;
    }

    // Getter và setter cho fullName
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getter và setter cho email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và setter cho studentID
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
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
    /**
     * check duplicate username from the library.
     * @param username the username to check duplicate.
     * @return true if duplicate username exists, false otherwise.
     * @throws SQLException thrown if an error occurs.
     */
    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Nếu có ít nhất 1 tài khoản với tên đăng nhập này
            }
        }
        return false;
    }

    /**
     * reigister the new user in the library.
     * @param id the id of the new user.
     * @param userName the name of the new user.
     * @param password the password of the new user.
     * @param role the role of the new user.
     * @param fullName the full name of the new user.
     * @param email the email of the new user.
     * @param number the number of the new user.
     * @return true if the user registered successfully, false otherwise.
     * @throws SQLException thrown if an error occurs.
     */
    private boolean registerUser(int id, String userName, String password, String role, String fullName, String email, String number) throws SQLException {
        String sql = "INSERT INTO users (id,username, password, role, fullName, email, number) VALUES (?,?,?,?,?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1,id);
            statement.setString(2, userName);
            statement.setString(3, password);
            statement.setString(4, role);
            statement.setString(5, fullName);
            statement.setString(6, email);
            statement.setString(7, number);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * check duplicate the id of student from the library.
     * @param id the id want check.
     * @return true if duplicate , false otherwise.
     * @throws SQLException thrown if an error occurs.
     */
    public boolean isStudentNumberTaken(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Nếu có ít nhất một bản ghi với mã sinh viên này
            }
        }
        return false;
    }

    /**
     * register the user in the library.
     * @throws SQLException thrown if an error occurs.
     */
    public void register() throws SQLException {
        try {
            if(isStudentNumberTaken(this.getId())) {
                showAlberDialog("Mã số sinh viên đã tồn tại. Vui lòng nhập mã khác.");
                return; }

            boolean check = registerUser(this.getId(), this.getUserName(), this.getPassword(), this.getRole(), this.getFullName(), this.getEmail(), this.getNumber());
            if (check) {
                this.showAlberDialog(this.getUserName() + " " + "Đăng ký thành công");
            } else {
                this.showAlberDialog("Đăng ký thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    /**
     * remove my review to the book in the library.
     * @param title the title of the book to be removed the review.
     * @param author the author of the book to be removed the review.
     * @param comment the comment of the book to be removed the review.
     * @throws SQLException thrown if an error occurs.
     */
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

    /**
     * get all users in the library.
     * @return the list of users.
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                // Update the constructor call to include all parameters
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getString("fullName"),  // Add fullName
                        resultSet.getString("email"),     // Add email
                        resultSet.getString("number")  // Add studentID
                ));
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

    /**
     * get role of the user.
     * @param username the username of the user.
     * @param password the password of the user.
     * @return the role of the user.
     * @throws SQLException thrown if an error occurs.
     */
    public static String getRoleUser(String username, String password) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("role"); // Trả về vai trò của người dùng
            } else {
                return "Incorrect username or password"; // Tên đăng nhập hoặc mật khẩu không đúng
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * show error message.
     * @param title the title of the error message.
     * @param message the error message.
     */
    public void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * show information dialog.
     * @param message the message to display.
     */
    public void showAlberDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
    /**
     * Lấy tất cả các đánh giá của sách từ cơ sở dữ liệu.
     *
     * @param book Đối tượng sách cần lấy đánh giá
     * @return Danh sách các đánh giá của sách, nếu có; nếu không có đánh giá, trả về null
     */
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

    /**
     * Chuyển đổi đối tượng User thành chuỗi.
     *
     * @return Chuỗi mô tả đối tượng User, bao gồm id, userName, password và role
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    /**
     * Cập nhật mã số sinh viên của người dùng dựa trên tên đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng cần cập nhật mã số sinh viên
     * @param MSSV Mã số sinh viên mới
     * @return true nếu cập nhật thành công, false nếu mã số sinh viên đã tồn tại
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình cập nhật
     */
    public boolean updateStudentMSVN(String username, int MSSV) throws SQLException {
        // Kiểm tra xem mã sinh viên mới đã tồn tại chưa
        if (isStudentNumberTaken(MSSV)) {
            showAlberDialog("Mã số sinh viên đã tồn tại. Vui lòng nhập mã khác.");
            return false;
        }

        // Cập nhật mã số sinh viên cho người dùng theo tên đăng nhập
        String sql = "UPDATE users SET id = ? WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, MSSV);
            statement.setString(2, username);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlberDialog("Cập nhật mã số sinh viên thành công!");
                return true;
            } else {
                showAlberDialog("Không tìm thấy người dùng với tên đăng nhập này.");
            }
        } catch (SQLException e) {
            showErrorDialog("Error", e.getMessage());
        }
        return false;
    }

    /**
     * Lấy mã số sinh viên của người dùng dựa trên tên đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng
     * @return Mã số sinh viên của người dùng
     * @throws SQLException Nếu không tìm thấy người dùng hoặc có lỗi xảy ra trong quá trình truy vấn
     */
    public static int getStudentIdByusername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id"); // Trả về mã sinh viên của người dùng
            } else {
                throw new SQLException("Không tìm thấy người dùng với tên đăng nhập: " + username); // Nếu không tìm thấy người dùng
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy mã sinh viên: " + e.getMessage(), e);
        }
    }

    /**
     * Tải chi tiết người dùng từ cơ sở dữ liệu theo mã sinh viên.
     *
     * @param studentID Mã sinh viên của người dùng
     * @return Đối tượng User chứa thông tin người dùng
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn cơ sở dữ liệu
     */
    public static User loadStudentDetailsByID(String studentID) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, Integer.parseInt(studentID));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Giả sử bảng của bạn có các cột: id, fullName, email, number
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getString("fullName"),
                        resultSet.getString("email"),
                        resultSet.getString("number"));
            }
        } catch (SQLException e) {
            throw e;
        }
        return user;
    }

    /**
     * Cập nhật thông tin người dùng trong cơ sở dữ liệu.
     *
     * @param fullName Tên đầy đủ của người dùng
     * @param phoneNumber Số điện thoại của người dùng
     * @param password Mật khẩu của người dùng
     * @param username Tên đăng nhập của người dùng cần cập nhật
     */
    public static void updateDatabase(String fullName, String phoneNumber, String password, String username) {
        String sql = "UPDATE users SET fullName = ?, number = ?, password = ? WHERE username = ?";

        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Gán giá trị vào các tham số của câu lệnh SQL
            stmt.setString(1, fullName);
            stmt.setString(2, phoneNumber);
            stmt.setString(3, password);
            stmt.setString(4, username);

            // Thực thi câu lệnh cập nhật
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Cập nhật thông tin thành công!");
            } else {
                System.out.println("Không tìm thấy người dùng với tên đăng nhập này.");
            }

        } catch (SQLException e) {
            // Xử lý lỗi và in ra thông báo nếu có lỗi
            e.printStackTrace();
        }
    }

    /**
     * Lấy chi tiết người dùng từ cơ sở dữ liệu theo tên đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng
     * @return Đối tượng User chứa thông tin người dùng
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy vấn
     */
    public static User loadUserDetailsByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Gán tham số tên đăng nhập vào câu lệnh SQL
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getString("fullName"),
                        resultSet.getString("email"),
                        resultSet.getString("number")
                );
            }
        } catch (SQLException e) {
            // Ghi lại lỗi hoặc ném lại để caller xử lý
            throw e;
        }

        return user;
    }

}
