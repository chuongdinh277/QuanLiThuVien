import Document.Book;
import Document.Transaction;
import Document.TransactionDAO;
import User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDAOTest {

    private TransactionDAO transactionDAO;

    @BeforeEach
    void setUp() {
        transactionDAO = new TransactionDAO();
    }

    @Test
    void testBorrowBook_Success() throws SQLException {
        User user = new User(1,"testUser", "Test User", "admin", "testUser", "email", "12345678");
        Book book = new Book("Test Book", "Test Author", "Fiction", 5, 5, "Description", "Publisher", "Section", "imagePath", "ISBN123");

        boolean result = TransactionDAO.borrowBook(user, book, 1, 7);
        assertFalse(result);
    }

    @Test
    void testBorrowBook_NotAvailable() throws SQLException {
        User user = new User(1,"testUser", "Test User", "admin", "testUser", "email", "12345678");
        Book book = new Book("Test Book", "Test Author", "Fiction", 0, 0, "Description", "Publisher", "Section", "imagePath", "ISBN123");


        boolean result = TransactionDAO.borrowBook(user, book, 1, 7);
        assertFalse(result);
    }

    @Test
    void testReturnBook_Success() throws SQLException {
        User user = new User(1,"testUser", "Test User", "admin", "testUser", "email", "12345678");
        Book book = new Book("Test Book", "Test Author", "Fiction", 5, 5, "Description", "Publisher", "Section", "imagePath", "ISBN123");

        boolean result = TransactionDAO.returnBook(user, book);
        assertFalse(result);
    }

    @Test
    void testReturnBook_NotBorrowed() throws SQLException {
        User user = new User(1,"testUser", "Test User", "admin", "testUser", "email", "12345678");
        Book book = new Book("Test Book", "Test Author", "Fiction", 5, 5, "Description", "Publisher", "Section", "imagePath", "ISBN123");

        boolean result = TransactionDAO.returnBook(user, book);
        assertFalse(result);
    }

    @Test
    void testGetTransactionByUserName() throws SQLException {
        User user = new User(1,"testUser", "Test User", "admin", "testUser", "email", "12345678");
        // Giả lập dữ liệu trả về
        List<Transaction> transactions = TransactionDAO.getTransactionByUserName(user);
        assertNotNull(transactions);
    }

    @Test
    void testDeleteTransaction() throws SQLException {
        int transactionId = 1;
        boolean result = TransactionDAO.deleteTransaction(transactionId);
        assertTrue(result); // Giả lập rằng có ít nhất một giao dịch bị xóa
    }

    @Test
    void testGetAllTransaction() throws SQLException {
        List<Transaction> transactions = TransactionDAO.getAllTransaction();
        assertNotNull(transactions);
    }

    @Test
    void testGetTransactionsByStudentId() throws SQLException {
        String studentId = "mssv";
        List<Transaction> transactions = TransactionDAO.getTransactionsByStudentId(studentId);
        assertNotNull(transactions);
    }
}