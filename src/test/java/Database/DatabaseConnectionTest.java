package Database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testGetConnection() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            assertNotNull(connection, "Kết nối không thành công, kết quả trả về là null.");
            connection.close(); // Đóng kết nối sau khi kiểm tra
        } catch (SQLException e) {
            fail("Kết nối thất bại: " + e.getMessage());
        }
    }
}