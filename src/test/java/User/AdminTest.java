package User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import User.User;
import User.Admin;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

class AdminTest {
    private Admin admin;

    @BeforeAll
    static void setUpJavaFx() {
        // Khởi chạy JavaFX trong một luồng riêng biệt nếu chưa khởi chạy
        if (!Platform.isFxApplicationThread()) {
            new Thread(() -> Application.launch(JavaFXTestApp.class)).start();
        }

        // Chờ JavaFX khởi tạo
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to initialize JavaFX", e);
        }
    }

    @BeforeEach
    void setUp() {
        // Khởi tạo đối tượng Admin trước mỗi bài kiểm tra
        admin = spy(new Admin("adminUser", "adminPass"));
    }

    @Test
    void testAddUser_Success() throws SQLException {
        User user = new User(1, "testUser", "testPass", "User", "Test User", "test@example.com", "1234567890");

        // Giả lập việc thêm người dùng và kiểm tra
        Platform.runLater(() -> {
            try {
                admin.addUser(user);
                assertTrue(admin.viewAllMembers().contains(user), "User should be added successfully.");
            } catch (Exception e) {
                fail("Error adding user: " + e.getMessage());
            }
        });
    }

    @Test
    void testRemoveUser_Success() throws SQLException {
        User user = new User(1, "testUser", "testPass", "User", "Test User", "test@example.com", "1234567890");

        Platform.runLater(() -> {
            try {
                admin.addUser(user); // Đảm bảo người dùng được thêm
                assertTrue(admin.viewAllMembers().contains(user), "User should exist before removal.");

                admin.removeUser(user);
                assertFalse(admin.viewAllMembers().contains(user), "User should be removed successfully.");
            } catch (Exception e) {
                fail("Error removing user: " + e.getMessage());
            }
        });
    }

    @Test
    void testFindUser_Success() throws SQLException {
        // Giả lập một người dùng cần tìm
        User mockUser = new User(1, "testUser", "testPass", "User", "Test User", "test@example.com", "1234567890");

        // Gọi phương thức công khai findUser và kiểm tra kết quả
        Platform.runLater(() -> {
            try {
                User result = admin.findUser(mockUser);
                assertNotNull(result, "User should be found.");
                assertEquals("testUser", result.getUserName(), "The username should match.");
                verify(admin).showAlberDialog("Tìm thấy testUser");
            } catch (IllegalArgumentException e) {
                fail("Eror find user: " + e.getMessage());
            }
    });
    }

    @Test
    void testFindUser_NotFound() throws SQLException {
        // Giả lập người dùng không tồn tại
        User mockUser = new User(1, "testUser", "testPass", "User", "Test User", "test@example.com", "1234567890");

        Platform.runLater(() -> {
           try {
               // Gọi hàm findUser và kiểm tra kết quả
               User result = admin.findUser(mockUser);

               assertNull(result, "User should not be found.");
               verify(admin).showAlberDialog("Không tìm thấy user");
           } catch (IllegalStateException expected) {
               fail("Error find user: " + expected.getMessage());
           }
        });
    }

    @Test
    void testViewAllMembers() throws SQLException {
        Platform.runLater(() -> {
            try {
                List<User> users = admin.viewAllMembers();
                assertNotNull(users, "The list of members should not be null.");
                assertTrue(users.size() >= 0, "The list of members should be valid.");
            } catch (Exception e) {
                fail("Error viewing all members: " + e.getMessage());
            }
        });
    }

    // Lớp JavaFX giả lập để khởi tạo môi trường JavaFX
    public static class JavaFXTestApp extends Application {
        @Override
        public void start(javafx.stage.Stage primaryStage) {
            Pane root = new Pane(); // Tạo root pane
            Scene scene = new Scene(root, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("JavaFX Test App");
            primaryStage.show();
        }
    }
}
