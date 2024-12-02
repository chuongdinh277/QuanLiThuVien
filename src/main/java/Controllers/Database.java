package Controllers;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Lớp Database cung cấp các phương thức để kết nối với cơ sở dữ liệu.
 * Quản lý kết nối thông qua thuộc tính databaseLink.
 */
public class Database {

    /**
     * Biến lưu trữ đối tượng kết nối cơ sở dữ liệu.
     */
    public Connection databaseLink;

    /**
     * Phương thức lấy đối tượng Connection để kết nối với cơ sở dữ liệu.
     * Sử dụng JDBC để kết nối với cơ sở dữ liệu MySQL.
     *
     * @return Đối tượng Connection nếu kết nối thành công, hoặc null nếu có lỗi.
     */
    public Connection getConnection() {
        String databasName = ""; // Tên cơ sở dữ liệu
        String databaseUser = ""; // Tên người dùng cơ sở dữ liệu
        String databasePassword = ""; // Mật khẩu người dùng cơ sở dữ liệu
        String url = "jdbc:mysql://localhost:3308/"; // URL kết nối đến cơ sở dữ liệu

        try {
            // Nạp trình điều khiển JDBC cho MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Tạo kết nối đến cơ sở dữ liệu
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            // In lỗi ra console nếu có lỗi trong quá trình kết nối
            e.printStackTrace();
        }
        return databaseLink;
    }
}
