package BookRating;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;

public class BookRatingDAO {
    public static List<BookRating> getBooksByKeyword(String keyword) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        List<BookRating> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Thêm dấu '%' vào từ khóa tìm kiếm
            String searchKeyword = "%" + keyword + "%"; // Tìm kiếm chứa keyword00
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BookRating book = new BookRating( resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("imagePath"),
                        resultSet.getDouble("average_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<BookRating> getBooksByTitle(String title) throws SQLException {
        String query = "SELECT * FROM books WHERE title =?";
        List<BookRating> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BookRating book = new BookRating(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("imagePath"),
                        resultSet.getDouble("average_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<BookRating> getBooksByAuthor(String author) throws SQLException {
        String query = "SELECT * FROM books WHERE author =?";
        List<BookRating> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, author);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BookRating book = new BookRating(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("imagePath"),
                        resultSet.getDouble("average_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
    public static List<BookRating> getBooksByCategory(String category) throws SQLException {
        String query = "SELECT * FROM books WHERE category =?";
        List<BookRating> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, category);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BookRating book = new BookRating(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("imagePath"),
                        resultSet.getDouble("average_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
    public static List<BookRating> getAvailableBooks() throws SQLException {
        List<BookRating> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE quantity > 0";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String categoryl = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                String imagePath = resultSet.getString("imagePath");
                double rating = resultSet.getDouble("average_rating");
                books.add(new BookRating(title, author, categoryl, quantity, description,imagePath,rating));
            }
        } catch (Exception e) {
            throw e;
        }
        return books;
    }

    public static List<BookRating> getTopRatedBooks() throws SQLException {
        List<BookRating> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY average_rating DESC LIMIT 15";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String categoryl = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                String imagePath = resultSet.getString("imagePath");
                double rating = resultSet.getDouble("average_rating");
                books.add(new BookRating(title, author, categoryl, quantity, description, imagePath, rating));
            }
        } catch (Exception e) {
            throw e;
        }
        return books;
    }

    public static List<BookRating> getRecentlyAddedBooks() throws SQLException {
        List<BookRating> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY created_at DESC LIMIT ";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String categoryl = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                String imagePath = resultSet.getString("imagePath");
                double rating = resultSet.getDouble("average_rating");
                books.add(new BookRating(title, author, categoryl, quantity, description, imagePath, rating));
            }

        } catch (Exception e) {
            throw e;
        }
        return books;
    }

}
