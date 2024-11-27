package Document;

import User.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;


public class TransactionDAO {

    //kiểm tra xem sách có đang được mượn ko 0
    private static boolean isBookBorrowed(Book book) throws SQLException {
        String sql = "SELECT remaining_book FROM books WHERE ISBN = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getISBN());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int remaining = resultSet.getInt("remaining_book");
                return remaining > 0;  // Sách có sẵn nếu remaining > 0
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    public static boolean borrowBook(User user, Book book, int quantity, int numberofdays) throws SQLException {
        // Kiểm tra xem sách có đủ số lượng cần mượn hay không
        if (!isBookBorrowed(book)) {
            return false;  // Sách không có sẵn hoặc không đủ số lượng
        }

        // Câu lệnh SQL để cập nhật số lượng sách còn lại trong `books`
        String updateSql = "UPDATE books SET remaining_book = remaining_book - ? WHERE ISBN = ? AND remaining_book >= ?";

        // Câu lệnh SQL để thêm giao dịch vào `transactions`
        String sql = "INSERT INTO transactions (username, title, author, ISBN, borrow_date, return_date, imagePath, quantity, mssv) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?)";

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
                int transactionAdded = insertTransactionStatement.executeUpdate();
                return transactionAdded > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

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
                        resultSet.getString("section"),
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
    public static boolean getBorrowedBooksbymssv(String mssv, Book currentBook) {
        String sql = "SELECT * FROM transactions WHERE mssv = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mssv);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (currentBook != null && resultSet.getString("ISBN").equals(currentBook.getISBN())) {
                    return true;
                } else {
                    System.out.println("Không tìm thấy sách trùng khớp");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


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

        return result;  // Trả về danh sách giao dịch của sinh viên
    }
}