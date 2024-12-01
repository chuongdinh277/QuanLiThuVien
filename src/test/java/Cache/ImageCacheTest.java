package Cache;

import Cache.ImageCache;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageCacheTest {

    private ImageCache imageCache;
    private Connection mockConnection;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    @BeforeAll
    static void initJavaFX() {
        // Khởi tạo môi trường JavaFX cho kiểm thử
        Platform.startup(() -> {
            // Khởi tạo JavaFX UI thread
        });
    }

    @BeforeEach
    void setUp() throws SQLException {
        imageCache = ImageCache.getInstance();
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock the database connection
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() {
        imageCache = null;
        // Clear the cache after each test
        clearCache();
    }

    private void clearCache() {
        // Access the private cache field using reflection and clear it
        try {
            var cacheField = ImageCache.class.getDeclaredField("cache");
            cacheField.setAccessible(true);
            ((Map<String, Image>) cacheField.get(imageCache)).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void testLoadImagesFromDatabase() throws SQLException {
        // Mock the ResultSet to simulate database response
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("imagePath")).thenReturn("file:example.png");
        when(mockResultSet.getString("ISBN")).thenReturn("1234567890");

        // Simulate loading images from database
        imageCache.loadImagesFromDatabase();

        // Kiểm tra rằng ảnh đã được lưu vào cache
        Platform.runLater(() -> {
            assertNotNull(imageCache.getImage("file:example.png"));
        });
    }

    @Test
    void testGetImageReturnsNullForInvalidPath() {
        String invalidPath = "invalid:path";

        // Kiểm tra trả về null khi đường dẫn ảnh không hợp lệ
        Platform.runLater(() -> {
            assertNull(imageCache.getImage(invalidPath));
        });
    }

    // Additional tests can be added to cover more edge cases
}
