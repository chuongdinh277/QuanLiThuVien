package Document;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 // Thay <package_name> bằng tên gói thực tế

import static Database.DatabaseConnection.getConnection;


public class BookDAO {
    public static boolean addBook(Book book) throws SQLException {
        String checkSql = "SELECT 1 FROM books WHERE title = ? AND author = ? AND category = ? AND description = ? AND publisher = ? AND section = ?";

        String updateSql = "UPDATE books SET quantity = quantity + ? WHERE title = ? AND author = ? AND category = ? AND description = ? AND imagePath = ? AND publisher = ? AND section = ?";

        String insertSql = "INSERT INTO books (title, author, category, quantity, description, imagePath, publisher, section) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                 PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                 PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

                checkStatement.setString(1, book.getTitle());
                checkStatement.setString(2, book.getAuthor());
                checkStatement.setString(3, book.getCategory());
                checkStatement.setString(4, book.getDescription());
                checkStatement.setString(5, book.getPublisher());
                checkStatement.setString(6, book.getSection());

                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    updateStatement.setInt(1, book.getQuantity());
                    updateStatement.setString(2, book.getTitle());
                    updateStatement.setString(3, book.getAuthor());
                    updateStatement.setString(4, book.getCategory());
                    updateStatement.setString(5, book.getDescription());
                    updateStatement.setString(6, book.getImagePath());
                    updateStatement.setString(7, book.getPublisher());
                    updateStatement.setString(8, book.getSection());

                    int rowsAffected = updateStatement.executeUpdate();
                    connection.commit();
                    return rowsAffected > 0;
                } else {
                    insertStatement.setString(1, book.getTitle());
                    insertStatement.setString(2, book.getAuthor());
                    insertStatement.setString(3, book.getCategory());
                    insertStatement.setInt(4, book.getQuantity());
                    insertStatement.setString(5, book.getDescription());
                    insertStatement.setString(6, book.getImagePath());
                    insertStatement.setString(7, book.getPublisher());
                    insertStatement.setString(8, book.getSection());

                    int rowsAffected = insertStatement.executeUpdate();
                    connection.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error during database operation: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
            throw e;
        }
    }

    public static boolean deleteBookByTitle(String title) throws SQLException {
        String sql = "DELETE FROM books WHERE title =?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public static boolean deleteBook(Book book) throws SQLException {
        String sql = "DELETE FROM books WHERE title = ? AND author = ? AND category = ? AND description = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setString(4, book.getDescription());
            statement.setString(5,book.getPublisher());
            statement.setString(6,book.getSection());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public static boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, category=?, quantity=?, description=?,imagePath=?, publisher=?, section=?,  WHERE title=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setInt(4, book.getQuantity());
            statement.setString(5, book.getDescription());
            statement.setString(6, book.getPublisher());
            statement.setString(7, book.getSection());
            statement.setString(8, book.getImagePath());
            statement.setString(9, book.getTitle());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public static boolean updateQuantity(Book book, int newQuantity) throws SQLException {
        String sql = "UPDATE books SET quantity=? WHERE title=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newQuantity);
            statement.setString(2, book.getTitle());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0) {
                book.setQuantity(newQuantity);
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public static Book searchBooksExact(String title, String author) throws SQLException {
        String query = "SELECT * FROM books WHERE title = ? AND author = ?";
        Book books = null; ;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                books = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"));
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
    public static List<Book> getBooksByKeyword(String keyword) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Thêm dấu '%' vào từ khóa tìm kiếm
            String searchKeyword = "%" + keyword + "%"; // Tìm kiếm chứa keyword
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<Book> getBooksByTitle(String title) throws SQLException {
        String query = "SELECT * FROM books WHERE title =?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<Book> getBooksByAuthor(String author) throws SQLException {
        String query = "SELECT * FROM books WHERE author =?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, author);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static double getBookRating(String title, String author) throws SQLException {
        String query = "SELECT average_rating FROM books WHERE title =? AND author =?";
        double rating = 0;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                rating = resultSet.getDouble("average_rating");
            }
        } catch (SQLException e) {
            throw e;
        }
        return rating;
    }
    public static List<Book> getBooksByCategory(String category) throws SQLException {
        String query = "SELECT * FROM books WHERE category =?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, category);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
    public static List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<Book>();
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
                String publisher = resultSet.getString("publisher");
                String section = resultSet.getString("section");
                books.add(new Book(title, author, categoryl, quantity, description, publisher,section,imagePath));
            }
        } catch (Exception e) {
            throw e;
        }
        return books;
    }
    public static boolean validateBookData(Book book) {
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            return false;
        }
        if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
            return false;
        }
        if (book.getQuantity() < 0) {
            return false;
        }
        return true;
    }

}