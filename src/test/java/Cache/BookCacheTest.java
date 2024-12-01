package Cache;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import Database.DatabaseConnection;
import Document.Book;
import Cache.BookCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class BookCacheTest {
    private Database.DatabaseConnection mockConnection;
    private BookCache bookCache;

    @BeforeEach
    public void setUp() {
        // Tạo đối tượng BookCache
        bookCache = BookCache.getInstance();
    }

    @Test
    void testGetBook() {
        String isbn = "978-3-16-148410-0";
        Book book = new Book("Test Title", "Test Author", "Fiction", 10, 5, "Description", "Publisher", "Section", "imagePath", isbn);
        BookCache.putBook(book);

        // Kiểm tra rằng cuốn sách có thể được lấy từ cache
        Book result = bookCache.getBook(isbn);
        assertNotNull(result);
        assertEquals(book, result);
    }

    @Test
    void testPutBook() {
        String isbn = "978-1-234-56789-0";
        Book book = new Book("Another Test Title", "Another Author", "Non-Fiction", 20, 15, "Another Description", "Another Publisher", "Section2", "imagePath", isbn);

        // Thêm sách vào cache
        BookCache.putBook(book);

        // Kiểm tra rằng sách đã được thêm vào cache
        assertTrue(BookCache.isCached(isbn));
    }

    @Test
    void testIsCached() {
        String isbn = "978-3-16-148410-0";
        Book book = new Book("Test Title", "Test Author", "Fiction", 10, 5, "Description", "Publisher", "Section", "imagePath", isbn);

        // Thêm sách vào cache
        BookCache.putBook(book);

        // Kiểm tra rằng sách đã được cache
        assertTrue(BookCache.isCached(isbn));

        // Kiểm tra một ISBN không tồn tại trong cache
        assertFalse(BookCache.isCached("non-existent-isbn"));
    }

    @Test
    void testSaveCacheToFile() {
        String isbn = "978-3-16-148410-0";
        Book book = new Book("Test Title", "Test Author", "Fiction", 10, 5, "Description", "Publisher", "Section", "imagePath", isbn);
        BookCache.putBook(book);

        // Lưu cache vào file
        bookCache.saveCacheToFile();

        // Kiểm tra xem tệp có tồn tại và được ghi đúng cách
        // Có thể sử dụng thư viện như Files.exists() để kiểm tra sự tồn tại của tệp hoặc
        // kiểm tra các phương thức liên quan đến file nếu cần thiết
    }

    @Test
    void testLoadCacheFromFile() {
        // Giả lập việc tải cache từ tệp
        bookCache.loadCacheFromFile();

        // Kiểm tra rằng cache không rỗng
        assertTrue(BookCache.isCached("978-3-16-148410-0"));
    }

    @Test
    void testClearCache() {
        String isbn = "978-3-16-148410-0";
        Book book = new Book("Test Title", "Test Author", "Fiction", 10, 5, "Description", "Publisher", "Section", "imagePath", isbn);
        BookCache.putBook(book);

        // Kiểm tra rằng sách đã được thêm vào cache
        assertTrue(BookCache.isCached(isbn));

        // Xóa cache và kiểm tra lại
        bookCache.clearCache();
        assertFalse(BookCache.isCached(isbn));
    }
}

