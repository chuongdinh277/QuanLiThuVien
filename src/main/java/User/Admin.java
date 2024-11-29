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

    public void changeRole(User user, String newRole) {
        try {
            boolean check = changeRole(user.getUserName(), user.getPassword(), user.getRole(), newRole);
            if (check) {
                this.showAlberDialog("Thay đổi thành công");
            } else {
                this.showAlberDialog("Thay đổi thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

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
                return new User(id,userName, password, role, fullName, email, number);
            }
            return null;
        } catch (SQLException e) {
            throw e;
        }
    }


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

                // In log kết quả (tuỳ chọn)
                System.out.println("Số bình luận đã xóa của username " + username + ": " + rowsDeleted);

                // Trả về true nếu có ít nhất một dòng bị xóa
                return rowsDeleted > 0;
            } catch (SQLException e) {
                System.err.println("Lỗi khi xóa bình luận của username: " + username);
                e.printStackTrace();
                throw e; // Ném lại lỗi để xử lý ở cấp cao hơn nếu cần
            }
        }



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
                        else System.out.println("null");
                        boolean transactionDeleted = TransactionDAO.deleteTransaction(transaction.getId());
                    }
                    // Hiển thị danh sách sách mượn trước khi xóa
                    this.showAlberDialog("Danh sách sách mượn:\n" + borrowedBooksList.toString());
                }
                boolean check = removeUserById(user.getId());
                boolean checkcomment = deleteCommentsByUsername(user.getUserName());
                if (check && checkcomment) {
                    this.showAlberDialog("Xóa thành công");
                } else {
                    this.showAlberDialog("Xóa thất bại");
                }
            } catch (SQLException e) {
                this.showErrorDialog("Error", e.getMessage());
            }
        }

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

                    User user = new User(id,username, password, role, fullName, email, number);
                    if(user.getNumber() != null && user.getEmail()!=null && user.getFullName() !=null) users.add(user);
                    // In ra thông tin người dùng đã thêm vào danh sách
                    System.out.println("Added user: " + fullName + ", " + email + ", " + number);
                }
                return users;
            } catch (SQLException e) {
                this.showErrorDialog("Error", e.getMessage());
            }
            return null;
        }



    public void addBook(Book book) {
        try {
            boolean result = BookDAO.addBook(book);
            if (result) {
                this.showAlberDialog("Thêm thành công");
            } else {
                this.showAlberDialog("Thêm thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }

    }


    public void removeBook(String title) {
        try {
            boolean result = BookDAO.deleteBookByTitle(title);
            if (result) {
                this.showAlberDialog("Xóa thành công");
            } else {
                this.showAlberDialog("Xóa thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    public void removeBook(Book book) {
        try {
            boolean result = BookDAO.deleteBook(book);
            if (result) {
                this.showAlberDialog("Xóa thành công");
            } else {
                this.showAlberDialog("Xóa thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    public void updateBook(Book book) {
        try {
            boolean result = BookDAO.updateBook(book);
            if (result) {
                this.showAlberDialog("Cập nhật thành công");
            } else {
                this.showAlberDialog("Cập nhật thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
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

    public void deleteReview(String username, String title, String author, String comment) throws SQLException {
        try {
            boolean check = ReviewDAO.removeReview(username, title, author, comment);
            if (check) {
                this.showAlberDialog("Xóa đánh giá thành công");
            } else {
                this.showAlberDialog("Xóa đánh giá thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    public void showAlberDialog(String message) {
        super.showAlberDialog(message);
    }

    public void showErrorDialog(String title, String message) {
        super.showErrorDialog(title, message);
    }

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

    public User getUserById(int studentId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                String fullName = resultSet.getString("fullName");
                String email = resultSet.getString("email");
                String number = resultSet.getString("number");

                User user = new User(id,username, password, role, fullName, email, number);
                return user;
            }
        }
        return null;  // Trả về null nếu không tìm thấy người dùng
    }


}