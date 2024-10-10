package Database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public final class DatabaseConnection {


    private static final String URL = "jdbc:mysql://localhost:3308/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "chuong@085920";

    private static DatabaseConnection handler = null;
    private static Connection conn = null;
    private static Statement statement = null;



    public static Connection getConnection() throws SQLException {
        //String PASSWORD = properties.getProperty("db.password");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối thành công!");
            } else {
                System.out.println("Kết nối thất bại!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
    }


}
