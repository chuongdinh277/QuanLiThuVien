package Document;

import User.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;


public class TransactionDAO {

    private static boolean isBookBorrowed(Book book) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE title = ? AND author = ? AND return_date IS NULL";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    public static boolean borrowBook(User user,Book book) throws SQLException {
        if (isBookBorrowed(book)) {
            return false;
        }
        String updateSql = "UPDATE books SET quantity = quantity - 1 WHERE title = ? AND author =? AND quantity >0";

        String sql = "INSERT INTO transactions (username, title, author, borrow_date) VALUES( ?, ?, ?, NOW())";
        try (Connection connection = getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateSql);
             PreparedStatement insertTransactionStatement = connection.prepareStatement(sql)) {
            updateStatement.setString(1, book.getTitle());
            updateStatement.setString(2, book.getAuthor());

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Nếu giảm số lượng thành công, thêm giao dịch mượn sách
                insertTransactionStatement.setString(1, user.getUserName());
                insertTransactionStatement.setString(2, book.getTitle());
                insertTransactionStatement.setString(3, book.getAuthor());

                int transactionAdded = insertTransactionStatement.executeUpdate();
                return transactionAdded > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
        return  false;
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
                result.add(new Transaction(user.getUserName(),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date")
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
                result.add(new Transaction(resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("imagePath"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("return_date")
                ));
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

}