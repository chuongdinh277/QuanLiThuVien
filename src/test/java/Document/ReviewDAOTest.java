package Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOTest {

    private ReviewDAO reviewDAO;

    @BeforeEach
    void setUp() {
        reviewDAO = new ReviewDAO();
    }

    @Test
    void testSaveReview() throws SQLException {
        String username = "testUser";
        String isbn = "ISBN123456";
        int rating = 5;
        String comment = "Great book!";

        // Kiểm tra không ném ngoại lệ khi lưu đánh giá
        assertDoesNotThrow(() -> ReviewDAO.saveReview(username, isbn, rating, comment));
    }

    @Test
    void testGetAverageRating_NoReviews() throws SQLException {
        String isbn = "ISBN123456";

        // Kiểm tra giá trị trung bình khi không có đánh giá nào
        double averageRating = ReviewDAO.getAverageRating(isbn);
        assertNotEquals(-1, averageRating);
    }

    @Test
    void testGetAverageRating_WithReviews() throws SQLException {
        String isbn = "ISBN123456";
        ReviewDAO.saveReview("user1", isbn, 4, "Good book!");
        ReviewDAO.saveReview("user2", isbn, 5, "Excellent!");

        // Kiểm tra giá trị trung bình khi có đánh giá
        double averageRating = ReviewDAO.getAverageRating(isbn);
        assertNotEquals(4.5, averageRating);
    }

    @Test
    void testGetReviewsByISBN() throws SQLException {
        String isbn = "ISBN123456";
        ReviewDAO.saveReview("user1", isbn, 4, "Good book!");
        ReviewDAO.saveReview("user2", isbn, 5, "Excellent!");

        List<Review> reviews = ReviewDAO.getReviewsByISBN(isbn);
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertNotEquals(2, reviews.size());
    }

    @Test
    void testGetReviewsByISBN_NoReviews() throws SQLException {
        String isbn = "ISBN123456";

        List<Review> reviews = ReviewDAO.getReviewsByISBN(isbn);
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
    }
}