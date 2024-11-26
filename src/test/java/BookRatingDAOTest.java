import BookRating.BookRatingDAO;
import BookRating.BookRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookRatingDAOTest {

    private BookRatingDAO bookRatingDAO;

    @BeforeEach
    void setUp() {
        bookRatingDAO = new BookRatingDAO();
    }

    @Test
    void testGetBooksByKeyword() throws SQLException {
        String keyword = "Test";
        List<BookRating> books = bookRatingDAO.getBooksByKeyword(keyword);
        assertNotNull(books);
    }

    @Test
    void testGetBooksByTitle() throws SQLException {
        String title = "The Great Gatsby";
        List<BookRating> books = bookRatingDAO.getBooksByTitle(title);
        assertNotNull(books);
        assertTrue(books.isEmpty());
        if (!books.isEmpty())
        assertEquals(title, books.get(0).getTitle());
    }

    @Test
    void testGetBooksByAuthor() throws SQLException {
        String author = "F. Scott Fitzgerald";
        List<BookRating> books = bookRatingDAO.getBooksByAuthor(author);
        assertNotNull(books);
        assertTrue(books.isEmpty());
        if (!books.isEmpty())
        assertEquals(author, books.get(0).getAuthor());
    }

    @Test
    void testGetBooksByCategory() throws SQLException {
        String category = "Fiction";
        List<BookRating> books = bookRatingDAO.getBooksByCategory(category);
        assertNotNull(books);
    }

    @Test
    void testGetAvailableBooks() throws SQLException {
        List<BookRating> books = bookRatingDAO.getAvailableBooks();
        assertNotNull(books);
        assertTrue(books.size() > 0); // Kiểm tra có ít nhất một cuốn sách có sẵn
    }

    @Test
    void testGetTopRatedBooks() throws SQLException {
        List<BookRating> books = bookRatingDAO.getTopRatedBooks();
        assertNotNull(books);
        assertTrue(books.size() <= 15); // Kiểm tra không quá 15 cuốn sách
    }

    @Test
    void testGetRecentlyAddedBooks() throws SQLException {
        List<BookRating> books = bookRatingDAO.getRecentlyAddedBooks();
        assertNotNull(books, "The list of books should not be null.");
        assertFalse(books.isEmpty(), "The list of books should not be empty.");
    }
}
