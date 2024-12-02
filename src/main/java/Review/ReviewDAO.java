package Review;

import Document.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseConnection.getConnection;

public class ReviewDAO {

    /**
     * Kiểm tra xem sách có tồn tại trong cơ sở dữ liệu hay không dựa trên tiêu đề và tác giả.
     *
     * @param title Tiêu đề của sách.
     * @param author Tên tác giả của sách.
     * @return true nếu sách tồn tại, false nếu không.
     * @throws Exception Nếu có lỗi xảy ra khi truy vấn cơ sở dữ liệu.
     */
    public static boolean isBookExists(String title, String author) throws Exception {
        String sql = "SELECT COUNT(*) AS count FROM books WHERE title = ? AND author = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, author);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
            return false;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Thêm đánh giá cho một cuốn sách vào cơ sở dữ liệu.
     *
     * @param username Tên người dùng đánh giá.
     * @param title Tiêu đề sách.
     * @param author Tên tác giả sách.
     * @param review Nội dung đánh giá.
     * @param rating Đánh giá sao từ 1 đến 5.
     * @return true nếu thêm đánh giá thành công, false nếu không.
     * @throws Exception Nếu sách không tồn tại hoặc có lỗi khi thêm đánh giá.
     */
    public static boolean addReview(String username, String title, String author, String review, int rating) throws Exception {
        if (!isBookExists(title, author)) {
            throw new Exception("Book not found.");
        }
        String sql = "INSERT INTO reviews (username, book_title, book_author, comment_text, rating) VALUES (?,?,?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, review);
            statement.setInt(5, rating);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Cập nhật đánh giá trung bình của sách sau khi có thay đổi về đánh giá.
     *
     * @param title Tiêu đề sách.
     * @param author Tên tác giả sách.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn hoặc cập nhật cơ sở dữ liệu.
     */
    private static void updateBookRating(String title, String author) throws SQLException {
        String sql = "SELECT COUNT(rating) AS total_reviews, SUM(rating) AS total_rating FROM reviews WHERE book_title = ? AND book_author = ?";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, author);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int totalReviews = resultSet.getInt("total_reviews");
                int totalRating = resultSet.getInt("total_rating");
                double newRating = (double) totalRating / totalReviews;
                sql = "UPDATE books SET average_rating = ? WHERE title =? AND author =?";
                try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
                    updateStatement.setDouble(1, newRating);
                    updateStatement.setString(2, title);
                    updateStatement.setString(3, author);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Xóa một đánh giá của người dùng đối với một cuốn sách.
     *
     * @param username Tên người dùng.
     * @param title Tiêu đề sách.
     * @param author Tên tác giả sách.
     * @param review Nội dung đánh giá cần xóa.
     * @return true nếu xóa đánh giá thành công, false nếu không.
     * @throws Exception Nếu có lỗi trong quá trình xóa đánh giá.
     */
    public static boolean removeReview(String username, String title, String author, String review) throws Exception {
        String sql = "DELETE FROM reviews WHERE username =? AND book_title =? AND book_author =? AND comment_text =?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, review);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Lấy tất cả các đánh giá của sách dựa trên tiêu đề và tác giả.
     *
     * @param book Sách cần lấy các đánh giá.
     * @return Danh sách các đánh giá của sách.
     * @throws Exception Nếu có lỗi xảy ra khi truy vấn cơ sở dữ liệu.
     */
    public static List<Reviews> getAllReviewsByBook(Book book) throws Exception {
        List<Reviews> reviews = new ArrayList<>();

        String sql = "SELECT * FROM reviews WHERE book_title =? AND book_author =?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Reviews review = new Reviews(resultSet.getString("username"),
                        resultSet.getString("book_title"),
                        resultSet.getString("book_author"),
                        resultSet.getString("comment_text"),
                        resultSet.getInt("rating"));
                reviews.add(review);
            }
        } catch (Exception e) {
            throw e;
        }
        return reviews;
    }
}
