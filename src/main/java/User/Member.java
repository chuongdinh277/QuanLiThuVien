package User;

import Database.DatabaseConnection;
import Document.Book;
import Document.BookDAO;
import Document.Transaction;
import Document.TransactionDAO;
import Review.ReviewDAO;
import Review.Reviews;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp đại diện cho người dùng thuộc loại "Member" (Thành viên).
 * Các chức năng bao gồm tìm kiếm sách, xem thông tin giao dịch của người dùng,
 * và quản lý sách của người dùng.
 */
public class Member extends User {
    private final static String MEMBER = "User";

    /**
     * Khởi tạo đối tượng Member với các thông tin cơ bản.
     *
     * @param id ID của người dùng.
     * @param userName Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     */
    public Member(int id, String userName, String password) {
        super(id, userName, password, MEMBER, "", "", "");
    }

    /**
     * Tìm kiếm sách theo tiêu đề và tác giả.
     *
     * @param title Tiêu đề sách cần tìm.
     * @param author Tác giả sách cần tìm.
     * @return Một đối tượng Book nếu tìm thấy sách, ngược lại trả về null.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    public Book searchBook(String title, String author) throws SQLException {
        try {
            Book foundBook = BookDAO.searchBooksExact(title, author);
            if (foundBook != null) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBook;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    /**
     * Tìm kiếm sách theo tiêu đề.
     *
     * @param title Tiêu đề sách cần tìm.
     * @return Danh sách các sách tìm thấy.
     */
    public List<Book> searchBookByTitle(String title) {
        try {
            List<Book> foundBooks = BookDAO.getBooksByTitle(title);
            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    /**
     * Xem thông tin các giao dịch của người dùng.
     *
     * @return Danh sách các giao dịch của người dùng.
     */
    public List<Transaction> viewTransactionByMine() {
        try {
            List<Transaction> transactions = TransactionDAO.getTransactionByUserName(this);
            if (!transactions.isEmpty()) {
                this.showAlberDialog("Xem thông tin đơn hàng");
                return transactions;
            } else {
                this.showAlberDialog("Không tìm thấy đơn hàng nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    /**
     * Lấy danh sách tất cả các sách có sẵn.
     *
     * @return Danh sách các sách có sẵn trong thư viện.
     */
    public List<Book> getAllBooks() {
        try {
            List<Book> allBooks = BookDAO.getAvailableBooks();
            if (!allBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return allBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    /**
     * Đăng ký người dùng mới vào hệ thống.
     *
     * @param userName Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     * @param role Vai trò của người dùng.
     * @param fullName Tên đầy đủ của người dùng.
     * @param email Địa chỉ email của người dùng.
     * @param studentID Mã sinh viên của người dùng.
     * @return `true` nếu đăng ký thành công, `false` nếu không thành công.
     * @throws SQLException Nếu có lỗi xảy ra khi thực hiện truy vấn.
     */
    private boolean registerUser(String userName, String password, String role, String fullName, String email, String studentID) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, fullName, email, number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.setString(4, fullName);
            statement.setString(5, email);
            statement.setString(6, studentID);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Đăng ký người dùng vào hệ thống.
     *
     * @throws SQLException Nếu có lỗi xảy ra khi thực hiện đăng ký.
     */
    @Override
    public void register() throws SQLException {
        try {
            boolean check = registerUser(this.getUserName(), this.getPassword(), this.getRole(), this.getFullName(), this.getEmail(), this.getNumber());
            if (check) {
                this.showAlberDialog(this.getRole() + " " + this.getUserName() + " " + "Đăng ký thành công");
            } else {
                this.showAlberDialog("Đăng ký thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    /**
     * Xem danh sách sách theo phân trang.
     *
     * @param offset Vị trí bắt đầu của trang.
     * @param limit Số lượng sách hiển thị trên mỗi trang.
     * @return Danh sách các sách trong trang hiện tại.
     * @throws SQLException Nếu có lỗi khi thực hiện truy vấn.
     */
    public List<Book> viewBooksPaginated(int offset, int limit) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books LIMIT ? OFFSET ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Book book = new Book(resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("category"),
                            resultSet.getInt("quantity"),
                            resultSet.getInt("remaining_book"),
                            resultSet.getString("description"),
                            resultSet.getString("publisher"),
                            resultSet.getString("imagePath"),
                            resultSet.getString("ISBN"));
                    books.add(book);
                }
            }
        }
        return books;
    }

    /**
     * Xem tất cả các sách có sẵn trong thư viện.
     *
     * @return Danh sách các sách có sẵn.
     * @throws SQLException Nếu có lỗi khi thực hiện truy vấn.
     */
    public List<Book> viewAllBooks() throws SQLException {
        try {
            List<Book> books = BookDAO.getAvailableBooks();
            return books;
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    /**
     * Hiển thị hộp thoại thông báo với thông điệp.
     *
     * @param message Thông điệp thông báo.
     */
    public void showAlberDialog(String message) {
        super.showAlberDialog(message);
    }

    /**
     * Hiển thị hộp thoại lỗi với tiêu đề và thông điệp lỗi.
     *
     * @param title Tiêu đề của lỗi.
     * @param message Thông điệp lỗi.
     */
    public void showErrorDialog(String title, String message) {
        super.showErrorDialog(title, message);
    }
}
