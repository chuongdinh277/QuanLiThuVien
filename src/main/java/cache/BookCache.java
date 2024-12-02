package Cache;

import Document.Book;
import Database.DatabaseConnection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookCache {
    private static BookCache instance; // Singleton instance
    private static final Map<String, Book> cache = new HashMap<>();
    private static final String CACHE_FILE = "book_cache.json"; // Đường dẫn tệp cache
    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // 2 luồng

    /**
     * constructor of the book cache.
     * It is private. It uses the design pattern : singleton instance.
     */
    private BookCache() {
        loadCacheFromFile(); // Tải dữ liệu từ tệp nếu tồn tại
        if (cache.isEmpty()) { // Nếu cache trống, tải từ cơ sở dữ liệu
            loadBooksFromDatabase();
        }
    }

    /**
     * get the instance. The instance is unique.
     * @return the instance.
     */
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
        executorService.submit(() -> {
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
                            resultSet.getString("imagePath"),
                            resultSet.getString("ISBN")
                    );
                    putBook(book);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi load sách từ cơ sở dữ liệu: " + e.getMessage(), e);
            }
        });
    }

    public void saveCacheToFile() {
        executorService.submit(() -> {
            try (FileWriter writer = new FileWriter(CACHE_FILE)) {
                Gson gson = new Gson();
                gson.toJson(cache.values(), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * write cache to the file in the Computer.
     */
    public void loadCacheFromFile() {
        executorService.submit(() -> {
            try (FileReader reader = new FileReader(CACHE_FILE)) {
                Gson gson = new Gson();
                Type bookListType = new TypeToken<List<Book>>() {}.getType();
                List<Book> books = gson.fromJson(reader, bookListType);
                for (Book book : books) {
                    putBook(book);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void clearCache() {
        cache.clear();
    }
}