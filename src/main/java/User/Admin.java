package User;

import Document.Book;
import Document.BookDAO;
import Document.Transaction;
import Document.TransactionDAO;
import Review.ReviewDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;


public class Admin extends User {
    private static final String ADMIN = "Admin";

    public Admin(String userName, String password) {
        super(0,userName, password, ADMIN,"","","");
    }

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu.
     *
     * @param username Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     * @param role Vai trò của người dùng (ví dụ: admin, user).
     * @param fullName Tên đầy đủ của người dùng.
     * @param email Địa chỉ email của người dùng.
     * @param number Số điện thoại của người dùng.
     * @return true nếu người dùng được thêm thành công, ngược lại false.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    private boolean addUser(String username, String password, String role, String fullName, String email, String number) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, fullName, email, number) VALUES (?,?,?,?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.setString(4, fullName);
            statement.setString(5, email);
            statement.setString(6, number);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Thêm người dùng mới vào cơ sở dữ liệu từ đối tượng User.
     *
     * @param user Đối tượng User chứa thông tin người dùng cần thêm.
     */
    public void addUser(User user) {
        try {
            boolean check = addUser(user.getUserName(), user.getPassword(), user.getRole(), user.getFullName(), user.getEmail(), user.getNumber());
            if (check) {
                this.showAlberDialog("Thêm thành công");
            } else {
                this.showAlberDialog("Thêm thất bại");
            }
        } catch (SQLException e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    /**
     * Thay đổi vai trò của người dùng trong cơ sở dữ liệu.
     *
     * @param username Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     * @param role Vai trò hiện tại của người dùng.
     * @param newRole Vai trò mới của người dùng.
     * @return true nếu thay đổi vai trò thành công, ngược lại false.
     * @throws Exception Nếu có lỗi trong quá trình thay đổi vai trò (ví dụ: vai trò không hợp lệ).
     */
    private boolean changeRole(String username, String password, String role, String newRole) throws Exception {
        if (role != newRole && role != ADMIN) {
            throw new Exception("Invalid role");
        }
        String sql = "UPDATE users SET userName = ?, password = ?, role = ? WHERE userName = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, newRole);
            statement.setString(4, username);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return false;
    }


    /**
     * Tìm người dùng trong cơ sở dữ liệu theo tên đăng nhập và mật khẩu.
     *
     * @param userName Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     * @return Đối tượng User nếu tìm thấy người dùng, ngược lại trả về null.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    private User findUserForDatabase(String userName, String password) throws SQLException {
        String sql = "SELECT * From users WHERE username = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String role = resultSet.getString("role");
                String fullName = resultSet.getString("fullName");
                String email = resultSet.getString("email");
                String number = resultSet.getString("number");
                return new User(id, userName, password, role, fullName, email, number);
            }
            return null;
        } catch (SQLException e) {
            throw e;
        }
    }


    /**
     * Tìm kiếm người dùng trong cơ sở dữ liệu bằng tên đăng nhập và mật khẩu.
     *
     * @param user Đối tượng User chứa thông tin tên đăng nhập và mật khẩu của người dùng cần tìm.
     * @return Trả về đối tượng User nếu tìm thấy người dùng trong cơ sở dữ liệu, ngược lại trả về null.
     * @throws RuntimeException Nếu có lỗi xảy ra trong quá trình tìm kiếm.
     */
    public User findUser(User user) {
        try {
            User userFound = findUserForDatabase(user.getUserName(), user.getPassword());
            if (userFound != null) {
                this.showAlberDialog("Tìm thấy " + user.getUserName());
                return userFound;
            } else {
                this.showAlberDialog("Không tìm thấy user");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xóa người dùng khỏi cơ sở dữ liệu theo ID người dùng.
     *
     * @param userId ID của người dùng cần xóa.
     * @return Trả về true nếu xóa thành công, ngược lại trả về false.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình thực thi câu lệnh SQL.
     */
    private boolean removeUserById(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);  // Sử dụng id để xóa người dùng
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Xóa các bình luận của người dùng từ bảng book_reviews theo tên đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng có bình luận cần xóa.
     * @return Trả về true nếu có ít nhất một bình luận bị xóa, ngược lại trả về false.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình xóa bình luận.
     * @throws IllegalArgumentException Nếu tên đăng nhập là null hoặc rỗng.
     */
    private boolean deleteCommentsByUsername(String username) throws SQLException {
        // Kiểm tra tham số đầu vào
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống hoặc null.");
        }

        // Câu truy vấn SQL để xóa các bình luận của người dùng
        String sql = "DELETE FROM book_reviews WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Gán tham số `username` vào câu truy vấn
            statement.setString(1, username);

            // Thực thi câu truy vấn
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bình luận của username: " + username);
            e.printStackTrace();
            throw e; // Ném lại lỗi để xử lý ở cấp cao hơn nếu cần
        }
    }

    /**
     * Xóa người dùng khỏi cơ sở dữ liệu và các bình luận của họ, đồng thời cập nhật lại thông tin sách và giao dịch.
     *
     * @param user Đối tượng User đại diện cho người dùng cần xóa.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình thực thi các câu lệnh SQL hoặc xử lý giao dịch.
     */
    public void removeUser(User user) throws SQLException {
        try {
            List<Transaction> transactions = TransactionDAO.getTransactionsByStudentId(String.valueOf(user.getId()));  // Lấy các giao dịch của sinh viên theo mã số sinh viên
            if (!transactions.isEmpty()) {
                StringBuilder borrowedBooksList = new StringBuilder("Sinh viên này còn đang mượn các sách sau:\n");
                for (Transaction transaction : transactions) {
                    borrowedBooksList.append("Sách: ").append(transaction.getTitle())
                            .append(", Số lượng: ").append(transaction.getQuantity())
                            .append("\n");
                    Book book = BookDAO.getBookByISBN(transaction.getIsbn());
                    if(book != null) {
                        // Cập nhật lại số lượng sách sau khi trả lại
                        boolean updated = BookDAO.updateQuantity(book, book.getQuantity() + transaction.getQuantity());
                        if (!updated) {
                            this.showAlberDialog("Cập nhật sách không thành công: " + book.getTitle());
                            return;
                        }
                    }
                    boolean transactionDeleted = TransactionDAO.deleteTransaction(transaction.getId());
                }
                // Hiển thị danh sách sách mượn trước khi xóa
                this.showAlberDialog("Danh sách sách mượn:\n" + borrowedBooksList.toString());
            }
            boolean check = removeUserById(user.getId());
            boolean checkcomment = deleteCommentsByUsername(user.getUserName());
        } catch (SQLException e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    /**
     * Lấy danh sách tất cả người dùng có vai trò là 'User' từ cơ sở dữ liệu.
     * Phương thức này truy vấn bảng `users` và chỉ lấy những người dùng có vai trò là 'User'.
     * Sau khi truy vấn thành công, danh sách người dùng hợp lệ sẽ được trả về.
     *
     * @return Một danh sách các đối tượng `User` chứa thông tin người dùng với vai trò là 'User'.
     *         Nếu có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu, phương thức sẽ trả về null.
     */
    public List<User> viewAllMembers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'User'";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                String fullName = resultSet.getString("fullName");
                String email = resultSet.getString("email");
                String number = resultSet.getString("number");

                User user = new User(id, username, password, role, fullName, email, number);
                if (user.getNumber() != null && user.getEmail() != null && user.getFullName() != null)
                    users.add(user);
            }
            return users;
        } catch (SQLException e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    public List<Book> viewAllBooks() throws SQLException {
        try {
            List<Book> books = BookDAO.getAvailableBooks();
            return books;
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public void showAlberDialog(String message) {
        super.showAlberDialog(message);
    }

    public void showErrorDialog(String title, String message) {
        super.showErrorDialog(title, message);
    }

    /**
     * Cập nhật thông tin của người dùng trong cơ sở dữ liệu.
     * Phương thức này thực hiện việc cập nhật các thông tin của người dùng bao gồm tên đầy đủ, số điện thoại,
     * email, mật khẩu và ID dựa trên tên đăng nhập của người dùng.
     *
     * @param user Đối tượng `User` chứa các thông tin người dùng mới cần cập nhật.
     * @return `true` nếu việc cập nhật thành công, ngược lại sẽ ném ra lỗi.
     * @throws SQLException Nếu có lỗi xảy ra khi thực thi câu lệnh SQL.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET fullName = ?, number = ?, email = ?, password = ?, id = ? WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Cập nhật các thông tin của người dùng
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getNumber());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());  // Cập nhật mật khẩu nếu cần
            statement.setInt(5, user.getId());  // Cập nhật ID nếu cần thay đổi
            statement.setString(6, user.getUserName());  // Sử dụng tên đăng nhập làm điều kiện WHERE

            int rowsUpdated = statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw e;
        }
    }

}