package User;

import Document.Book;
import Document.Transaction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private static Member member;

    @BeforeAll
    static void setUpJavaFX() {
        // Khởi chạy JavaFX Application Thread nếu chưa có
        new Thread(() -> Application.launch(JavaFXTestApp.class)).start();
        try {
            Thread.sleep(500); // Chờ JavaFX khởi tạo
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to initialize JavaFX", e);
        }

        member = new Member(1, "testUser", "password123");
    }

    @Test
    void testSearchBook() {
        Platform.runLater(() -> {
            try {
                Book foundBook = member.searchBook("Test Book", "Test Author");
                assertNotNull(foundBook);
                assertEquals("Test Book", foundBook.getTitle());
            } catch (SQLException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchBookByTitle() {
        Platform.runLater(() -> {
            List<Book> books = member.searchBookByTitle("Test Book");
            assertNotNull(books);
            assertFalse(books.isEmpty());
        });
    }

    @Test
    void testViewTransactionByMine() {
        Platform.runLater(() -> {
            List<Transaction> transactions = member.viewTransactionByMine();
            assertNotNull(transactions);
        });
    }


    @Test
    void testGetAllBooks() {
        Platform.runLater(() -> {
            List<Book> allBooks = member.getAllBooks();
            assertNotNull(allBooks);
        });
    }

    @Test
    void testRegister() {
        Platform.runLater(() -> assertDoesNotThrow(() -> member.register()));
    }

    @Test
    void testViewBooksPaginated() {
        Platform.runLater(() -> {
            try {
                List<Book> books = member.viewBooksPaginated(0, 10);
                assertNotNull(books);
                assertTrue(books.size() <= 10);
            } catch (SQLException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        });
    }

    // Lớp ứng dụng JavaFX giả lập
    public static class JavaFXTestApp extends Application {
        @Override
        public void start(javafx.stage.Stage primaryStage) {
            // Tạo một root pane làm thành phần gốc của Scene
            Pane root = new Pane();

            // Tạo Scene và gắn root vào Scene
            Scene scene = new Scene(root, 1000, 600);

            // Gắn Scene vào Stage
            primaryStage.setScene(scene);

            // Cài đặt tiêu đề cho Stage
            primaryStage.setTitle("My JavaFX Application");

            // Hiển thị Stage
            primaryStage.show();
        }
    }
}
