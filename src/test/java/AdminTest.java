import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import User.User;
import User.Admin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        admin = new Admin("adminUser", "adminPass");
    }

    @Test
    void testAddUser_Success() throws SQLException {
        User user = new User(1, "testUser", "testPass", "User", "Test User", "test@example.com", "1234567890");

        // Giả lập việc thêm người dùng vào hệ thống và kiểm tra sau khi thêm
        Platform.runLater(() -> {
            try {
                admin.addUser(user);
                assertTrue(admin.viewAllMembers().contains(user));
            } catch (Exception e) {
                fail("Error adding user: " + e.getMessage());
            }
        });
    }

    @Test
    void testChangeRole_Success() throws Exception {
        User user = new User(1, "testUser", "User", "NewRole", "Test User", "test@example.com", "1234567890");

        // Giả lập thay đổi vai trò người dùng và kiểm tra sau khi thay đổi
        Platform.runLater(() -> {
            try {
                admin.changeRole(user, "Admin");
                assertEquals("Admin", user.getRole());
            } catch (Exception e) {
                fail("Error changing role: " + e.getMessage());
            }
        });
    }

    @Test
    void testRemoveUser_Success() throws SQLException {
        User user = new User(1, "testUser", "testPass", "User", "Test User", "test@example.com", "1234567890");

        // Thêm người dùng vào hệ thống và kiểm tra
        Platform.runLater(() -> {
            try {
                admin.addUser(user);
                assertTrue(admin.viewAllMembers().contains(user));

                admin.removeUser(user);
                assertFalse(admin.viewAllMembers().contains(user));
            } catch (SQLException e) {
                fail("Error removing user: " + e.getMessage());
            }
        });
    }

    @Test
    void testViewAllMembers() throws SQLException {
        List<User> users = admin.viewAllMembers();

        // Kiểm tra xem danh sách thành viên không rỗng
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }


    // Lớp JavaFX giả lập để khởi tạo môi trường JavaFX
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
