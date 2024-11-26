package Recomend;

import BookRating.BookRating;
import BookRating.BookRatingDAO;
import User.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;

public class Recommend {
    private static List<BookRating> getFavoriteBook(String username) throws SQLException {
        List<BookRating> books = new ArrayList<BookRating>();
        String result = null;
        String sql = "SELECT category, COUNT(category) AS count FROM books "
                + "JOIN transactions ON books.title = transactions.title AND books.author = transactions.author "
                + "WHERE transactions.username = ? "
                + "GROUP BY category "
                + "ORDER BY count DESC "
                + "LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("category");
            }
        } catch (SQLException e) {
            throw e;
        }
        if(result != null) {
            books = BookRatingDAO.getBooksByCategory(result);
        }
        return books;
    }

    private static boolean hasBorrowedBooks(User user) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUserName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    private static List<BookRating> getRecommendBooksByRating() throws SQLException {
        List<BookRating> books = new ArrayList<BookRating>();
        String sql = "SELECT * FROM books WHERE quantity > 0 ORDER BY average_rating DESC LIMIT 10";  // Corrected DESC
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                books.add(new BookRating(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("imagePath"),
                        resultSet.getDouble("average_rating")));
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<BookRating> getRecommendBooks(User user) throws SQLException {
        List<BookRating> books = new ArrayList<>();
        try {
            if (!hasBorrowedBooks(user)) {
                books = getFavoriteBook(user.getUserName());
            } else {
                books = getRecommendBooksByRating();
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
}
