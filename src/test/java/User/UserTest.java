package User;

import Document.Book;
import Review.Reviews;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static APIGoogle.GoogleBooksAPI.showErrorDialog;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;


    @BeforeAll
    static void setUpJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            new Thread(() -> Application.launch(AdminTest.JavaFXTestApp.class)).start();
        }
    }


    @BeforeEach
    void setUp() {
        user = new User(1, "testUser", "password123", "student", "Test User", "test@example.com", "123456789");
    }

    @Test
    void testIsUsernameTaken() throws SQLException {
        assertFalse(user.isUsernameTaken("newUser"));
    }

    @Test
    void testRegister() throws SQLException {
        Platform.runLater(() -> {
            try {
                // Thực hiện các thao tác đăng ký của bạn tại đây
                assertDoesNotThrow(() -> user.register());
            } catch (Exception e) {
                showErrorDialog( e.getMessage());
            }
        });
    }


    @Test
    void testDeleteReview() throws SQLException {
        Platform.runLater(() -> {
            try {
                assertDoesNotThrow(() -> user.deleteReview("Book Title", "Book Author", "Comment"));
            } catch (Exception e) {
                showErrorDialog( e.getMessage());
            }
        });
    }


    @Test
    void testGetAllUsers() {
        List<User> users = User.getAllUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void testGetRoleUser() throws SQLException {
        String role = User.getRoleUser("testUser", "password123");
        assertNotEquals("student", role);
    }

    @Test
    void testGetAllReviewsByBook() {
        Book book = new Book("Test Book", "Test Author", "Fiction", 5, 5, "Description", "Publisher", "Section", "ISBN");
        Platform.runLater(() -> {
            List<Reviews> reviews = user.getAllReviewsByBook(book);
            assertNull(reviews);
        });
    }

    @Test
    void testUpdateStudentMSVN() throws SQLException {
        Platform.runLater(() -> {
            boolean result = false;
            try {
                result = user.updateStudentMSVN("testUser", 2);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            assertTrue(result);
        });
    }

    @Test
    void testLoadStudentDetailsByID() throws SQLException {
        Platform.runLater(() -> {
            User loadedUser = null;
            try {
                loadedUser = User.loadStudentDetailsByID("1");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            assertNotNull(loadedUser);
            assertEquals("testUser", loadedUser.getUserName());
        });
    }
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