package Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;

public class ReviewDAO {

    /**
     * Lưu đánh giá của người dùng cho một cuốn sách vào cơ sở dữ liệu.
     *
     * @param username Tên người dùng thực hiện đánh giá.
     * @param isbn Mã ISBN của cuốn sách được đánh giá.
     * @param rating Số sao đánh giá (từ 1 đến 5).
     * @param comment Nội dung bình luận của người dùng.
     * @throws SQLException Nếu có lỗi trong quá trình lưu vào cơ sở dữ liệu.
     */
    public static void saveReview(String username, String isbn, int rating, String comment) throws SQLException {
        Connection connection = getConnection(); // Kết nối với cơ sở dữ liệu của bạn
        String query = "INSERT INTO book_reviews (username, isbn, rating, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, isbn);
            statement.setInt(3, rating);
            statement.setString(4, comment);
            statement.executeUpdate();
        }
    }

    /**
     * Lấy đánh giá trung bình của một cuốn sách dựa trên ISBN.
     *
     * @param isbn Mã ISBN của cuốn sách cần lấy đánh giá.
     * @return Điểm trung bình của cuốn sách, hoặc -1 nếu không có đánh giá nào.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    public static double getAverageRating(String isbn) throws SQLException {
        double averageRating = -1; // Trả về -1 nếu không có đánh giá nào
        String query = "SELECT SUM(rating) AS total_rating, COUNT(*) AS total_reviews FROM book_reviews WHERE isbn = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int totalRating = rs.getInt("total_rating"); // Tổng số sao của tất cả đánh giá
                int totalReviews = rs.getInt("total_reviews"); // Số lượng đánh giá

                if (totalReviews > 0) {
                    averageRating = (double) totalRating / totalReviews; // Tính số sao trung bình
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return averageRating; // Trả về số sao trung bình, hoặc -1 nếu không có đánh giá
    }

    /**
     * Lấy danh sách các đánh giá cho một cuốn sách dựa trên ISBN.
     *
     * @param isbn Mã ISBN của cuốn sách cần lấy đánh giá.
     * @return Danh sách các đối tượng Review chứa thông tin về người dùng, số sao và bình luận.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    public static List<Review> getReviewsByISBN(String isbn) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT username, rating, comment FROM book_reviews WHERE isbn = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    int rating = rs.getInt("rating");
                    String comment = rs.getString("comment");

                    // Tạo đối tượng Review từ kết quả truy vấn và thêm vào danh sách
                    reviews.add(new Review(username, rating, comment));
                }
            }
        }
        return reviews;
    }
}
