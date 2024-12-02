package Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDAOTest {

    private BookDAO bookDAO;

    @BeforeEach
    void setUp() {
        bookDAO = new BookDAO();
    }

    @Test
    void testAddBook() throws SQLException {
        Book book = new Book("Test Book", "Test Author", "Fiction", 10, 10, "Description", "Publisher", "Section", "ISBN123");
        boolean result = BookDAO.addBook(book);
        assertTrue(result);
    }

    @Test
    void testDeleteBookByTitle() throws SQLException {
        boolean result = BookDAO.deleteBookByTitle("Test Book");
        assertFalse(result);
    }

    @Test
    void testDeleteBook() throws SQLException {
        Book book = new Book("Test Book", "Test Author", "Fiction", 10, 10, "Description", "Publisher", "Section", "ISBN123");
        BookDAO.addBook(book); // Thêm sách trước khi xóa
        boolean result = BookDAO.deleteBook(book);
        assertTrue(result);
    }

    @Test
    void testUpdateBook() throws SQLException {
        Book book = new Book("Test Book", "Test Author", "Fiction", 10, 10, "Description", "Publisher", "Section", "ISBN123");
        BookDAO.addBook(book); // Thêm sách trước khi cập nhật
        book.setDescription("Updated Description");
        boolean result = BookDAO.updateBook(book);
        assertTrue(result);
    }

    @Test
    void testSearchBooksExact() throws SQLException {
        Book book = new Book("Test Book", "Test Author", "Fiction", 10, 10, "Description", "Publisher", "Section", "ISBN123");
        BookDAO.addBook(book); // Thêm sách trước khi tìm kiếm
        Book foundBook = BookDAO.searchBooksExact("Test Book", "Test Author");
        assertNotNull(foundBook);
        assertEquals("Test Book", foundBook.getTitle());
    }

    @Test
    void testGetBooksByKeyword() throws SQLException {
        Book book = new Book("Keyword Book", "Test Author", "Fiction", 10, 10, "Description", "Publisher", "Section", "ISBN123");
        BookDAO.addBook(book); // Thêm sách trước khi tìm kiếm
        List<Book> books = BookDAO.getBooksByKeyword("Keyword");
        assertFalse(books.isEmpty());
        assertEquals("Keyword Book", books.get(0).getTitle());
    }

    @Test
    void testGetAvailableBooks() throws SQLException {
        List<Book> books = BookDAO.getAvailableBooks();
        assertNotNull(books);
    }

    @Test
    void testUpdateQuantity() throws SQLException {
        Book book = new Book("Test Book", "Test Author", "Fiction", 10, 10, "Description", "Publisher", "Section", "ISBN123");
        BookDAO.addBook(book); // Thêm sách trước khi cập nhật số lượng
        boolean result = BookDAO.updateQuantity(book, 15);
        assertTrue(result);
        assertEquals(15, book.getQuantity());
    }
}