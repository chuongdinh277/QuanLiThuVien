package Document;

import User.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;


public class TransactionDAO {

    /**
     * Kiểm tra xem sách có thể được mượn hay không dựa trên số lượng còn lại.
     *
     * @param book Đối tượng sách cần kiểm tra.
     * @return true nếu sách có thể mượn (số lượng còn lại lớn hơn 0), false nếu không.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    private static boolean isBookBorrowed(Book book) throws SQLException {
        String sql = "SELECT remaining_book FROM books WHERE ISBN = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getISBN());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int remaining = resultSet.getInt("remaining_book");
                return remaining > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    /**
     * Mượn sách từ thư viện.
     *
     * @param user        Người mượn sách.
     * @param book        Sách muốn mượn.
     * @param quantity    Số lượng sách mượn.
     * @param numberofdays Số ngày mượn sách.
     * @return true nếu mượn sách thành công, false nếu không.
     * @throws SQLException Nếu có lỗi trong quá trình xử lý cơ sở dữ liệu.
     */
    public static boolean borrowBook(User user, Book book, int quantity, int numberofdays) throws SQLException {

        if (!isBookBorrowed(book)) {
            return false;
        }

        String updateSql = "UPDATE books SET remaining_book = remaining_book - ? WHERE ISBN = ? AND remaining_book >= ?";

        String sql = "INSERT INTO transactions (username, title, author, ISBN, borrow_date, return_date, imagePath, quantity, mssv, description) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateSql);
             PreparedStatement insertTransactionStatement = connection.prepareStatement(sql)) {

            // Giảm số lượng sách trong bảng `books`
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, book.getISBN());
            updateStatement.setInt(3, quantity);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Tính toán ngày trả sách dựa trên số ngày mượn
                LocalDate borrowDate = LocalDate.now();
                LocalDate returnDate = borrowDate.plusDays(numberofdays);

                // Thêm giao dịch mượn sách vào bảng `transactions`
                insertTransactionStatement.setString(1, user.getFullName());
                insertTransactionStatement.setString(2, book.getTitle());
                insertTransactionStatement.setString(3, book.getAuthor());
                insertTransactionStatement.setString(4, book.getISBN());
                insertTransactionStatement.setDate(5, Date.valueOf(returnDate));  // Ngày trả
                insertTransactionStatement.setString(6, book.getImagePath());
                insertTransactionStatement.setInt(7, quantity);
                insertTransactionStatement.setString(8, String.valueOf(user.getId()));
                insertTransactionStatement.setString(9, book.getDescription());
                int transactionAdded = insertTransactionStatement.executeUpdate();
                return transactionAdded > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    /**
     * Trả sách đã mượn.
     *
     * @param user Người trả sách.
     * @param book Sách cần trả.
     * @return true nếu trả sách thành công, false nếu không.
     * @throws SQLException Nếu có lỗi trong quá trình xử lý cơ sở dữ liệu.
     */
    public static boolean returnBook(User user, Book book) throws SQLException {
        // Câu lệnh SQL để cập nhật ngày trả sách trong bảng transactions
        if (isBookBorrowed(book)) {
            String updateBook = "UPDATE books SET quantity = quantity + 1 WHERE title = ? AND author =? AND quantity >0";
            String sql = "UPDATE transactions SET return_date = NOW() WHERE username = ? AND title = ? AND author = ? AND return_date IS NULL";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 PreparedStatement updateBookStatement = connection.prepareStatement(updateBook)) {
                statement.setString(1, user.getUserName());
                statement.setString(2, book.getTitle());
                statement.setString(3, book.getAuthor());

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Nếu cập nhật thành công, cập nhật số lượng thư trả về sách
                    updateBookStatement.setString(1, book.getTitle());
                    updateBookStatement.setString(2, book.getAuthor());
                    int updateBookQuantity = updateBookStatement.executeUpdate();
                    return updateBookQuantity > 0;
                }

            } catch (SQLException e) {
                throw e;
            }
        }
        return false;
    }

    /**
     * Lấy thông tin giao dịch của một sách dựa trên ISBN và mã sinh viên.
     *
     * @param ISBN  Mã ISBN của sách.
     * @param mssv  Mã sinh viên.
     * @return Giao dịch sách nếu tìm thấy, null nếu không tìm thấy.
     */
    public static Transaction getTransactionByISBNAndMssv(String ISBN, String mssv) {
        String sql = "SELECT * FROM transactions WHERE ISBN='" + ISBN + "' AND mssv='" + mssv + "'";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Nếu tìm thấy giao dịch, trả về một đối tượng Transaction
            if (resultSet.next()) {
                return new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("isbn"),
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("mssv")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transaction: " + e.getMessage(), e);
        }

        // Nếu không tìm thấy giao dịch, trả về null
        return null;
    }

    /**
     * Lấy tất cả giao dịch của một người dùng.
     *
     * @param user Người dùng cần lấy giao dịch.
     * @return Danh sách các giao dịch của người dùng.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    public static List<Transaction> getTransactionByUserName(User user) throws SQLException {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUserName());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new Transaction(
                        resultSet.getInt("transaction_id"),  // changed from id to transaction_id
                        resultSet.getString("isbn"),
                        user.getUserName(),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("mssv")  // added mssv column
                ));
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * Lấy tất cả giao dịch trong hệ thống.
     *
     * @return Danh sách tất cả giao dịch.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    public static List<Transaction> getAllTransaction() throws SQLException {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new Transaction(
                        resultSet.getInt("transaction_id"),  // changed from id to transaction_id
                        resultSet.getString("isbn"),
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("mssv")  // added mssv column
                ));
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * Lấy danh sách các sách đã được mượn bởi một sinh viên dựa trên mã sinh viên.
     *
     * @param mssv Mã sinh viên.
     * @return Danh sách các sách đã được mượn.
     */
    public static List<Book> getBorrowedBooks(String mssv) {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE mssv = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mssv);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN")
                );
                result.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * Kiểm tra xem sách có được mượn bởi sinh viên với MSSV cụ thể hay không.
     *
     * Phương thức này thực hiện truy vấn cơ sở dữ liệu để kiểm tra xem có giao dịch mượn sách nào
     * đối với sinh viên có MSSV được cung cấp và sách hiện tại (theo ISBN) hay không.
     *
     * @param mssv MSSV của sinh viên cần kiểm tra.
     * @param currentBook Sách cần kiểm tra, nếu không phải sách hiện tại, phương thức sẽ trả về false.
     * @return true nếu sinh viên với MSSV đã mượn sách hiện tại, false nếu không.
     */
    public static boolean getBorrowedBooksByMssv(String mssv, Book currentBook) {
        String sql = "SELECT * FROM transactions WHERE mssv = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mssv);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (currentBook != null && resultSet.getString("ISBN").equals(currentBook.getISBN())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * Lấy danh sách giao dịch của sinh viên từ cơ sở dữ liệu dựa trên MSSV của sinh viên.
     *
     * Phương thức này truy vấn cơ sở dữ liệu để lấy tất cả các giao dịch mà sinh viên có MSSV
     * tương ứng đã thực hiện. Dữ liệu giao dịch được lưu vào một danh sách và trả về cho người gọi.
     *
     * @param studentId MSSV của sinh viên cần lấy danh sách giao dịch.
     * @return Danh sách các giao dịch của sinh viên tương ứng.
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn cơ sở dữ liệu.
     */
    public static List<Transaction> getTransactionsByStudentId(String studentId) throws SQLException {
        List<Transaction> result = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE mssv = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result.add(new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("isbn"),
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("mssv")
                ));
            }
        } catch (SQLException e) {
            throw e;  // Quản lý ngoại lệ
        }

        return result;  // Trả về danh sách giao dịch của sinh viên
    }

    /**
     * Xóa một giao dịch khỏi cơ sở dữ liệu dựa trên ID giao dịch.
     *
     * Phương thức này xóa giao dịch có `transaction_id` tương ứng từ cơ sở dữ liệu. Nếu giao dịch
     * được xóa thành công, phương thức trả về giá trị `true`; ngược lại trả về `false`.
     *
     * @param transactionId ID của giao dịch cần xóa.
     * @return true nếu giao dịch đã được xóa thành công, false nếu không.
     * @throws SQLException Nếu có lỗi xảy ra khi thực hiện xóa giao dịch từ cơ sở dữ liệu.
     */
    public static boolean deleteTransaction(int transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, transactionId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Lấy danh sách giao dịch của sách từ cơ sở dữ liệu dựa trên ISBN của sách.
     *
     * Phương thức này truy vấn cơ sở dữ liệu để lấy tất cả các giao dịch mà sách có ISBN
     * tương ứng đã thực hiện. Dữ liệu giao dịch được lưu vào một danh sách và trả về cho người gọi.
     *
     * @param isbn ISBN của sách cần lấy danh sách giao dịch.
     * @return Danh sách các giao dịch liên quan đến sách có ISBN tương ứng.
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn cơ sở dữ liệu.
     */
    public static List<Transaction> getTransactionsByISBN(String isbn) throws SQLException {
        List<Transaction> result = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE isbn = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, isbn);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result.add(new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("isbn"),
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("mssv")
                ));
            }
        } catch (SQLException e) {
            throw e;  // Quản lý ngoại lệ
        }

        return result;  // Trả về danh sách giao dịch của sách
    }

}