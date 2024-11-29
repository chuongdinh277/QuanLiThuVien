package cache;

import Document.Book;
import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class BookCache {
    private static BookCache instance; // Singleton instance
    private static final Map<String, Book> cache = new HashMap<>();

    private BookCache() {
        loadBooksFromDatabase(); // Tự động tải dữ liệu khi khởi tạo
    }

    public static BookCache getInstance() {
        if (instance == null) {
            instance = new BookCache();
        }
        return instance;
    }

    public Book getBook(String isbn) {
        return cache.get(isbn);
    }

    public static void putBook(Book book) {
        cache.put(book.getISBN(), book);
    }

    public static boolean isCached(String isbn) {
        return cache.containsKey(isbn);
    }

    public void loadBooksFromDatabase() {
        System.out.println("Bat dau load sach");
        cache.clear();
        String sql = "SELECT * FROM books";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("remaining_Book"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN")
                );
                putBook(book);
            }
            System.out.println("Load sach thanh cong");
        } catch (SQLException e) {
            throw new RuntimeException("Error loading books from database: " + e.getMessage(), e);
        }
    }

    public void clearCache() {
        cache.clear();
    }
}
