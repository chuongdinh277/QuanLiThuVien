package controllers;

import Document.Book;
import Document.ReviewDAO;
import Document.TransactionDAO;
import User.User;
import User.currentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import Document.Review;
import javax.security.auth.callback.LanguageCallback;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class UserSeeBookDetails {

    @FXML
    private Button backButton;

    @FXML
    private Label bookAuthorLabel;

    @FXML
    private ImageView bookImage;

    @FXML
    private Button bookInfoButton;

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Button buttonBorrowBook;

    @FXML
    private AnchorPane infoPane;

    @FXML
    private Button viewCommentsButton;


    @FXML
    private Label bookDescriptionLabel;

    @FXML
    private Book currentBook;
    @FXML
    private Label bookSectionLabel;
    @FXML
    private Label ISBNLabel;
    @FXML
    private Button viewBook;
    @FXML
    private Button commentBook;
    @FXML
    private Pane viewBookPane;
    @FXML
    private Pane commentPane;
    @FXML
    private TextField quantityLabel;
    @FXML
    private TextField numberOfday;
   @FXML
   private ImageView star1;
    @FXML
    private ImageView star2;
    @FXML
    private ImageView star3;
    @FXML
    private ImageView star4;
    @FXML
    private ImageView star5;
    @FXML
    private ImageView starbook1;
    @FXML
    private ImageView starbook2;
    @FXML
    private ImageView starbook3;
    @FXML
    private ImageView starbook4;
    @FXML
    private ImageView starbook5;
    @FXML
    private VBox commentVbox;
    @FXML
    private Button commentButton;
    private int selectedRating = 0;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private void showViewBook(ActionEvent event) {
        viewBookPane.setVisible(true);
        commentPane.setVisible(false);
    }
    @FXML
    private void showCommentBook(ActionEvent event) {
        viewBookPane.setVisible(false);
        commentPane.setVisible(true);
        if(currentBook != null ) System.out.println(currentBook.getISBN());
        loadReview();
        System.out.println("hello");

    }

    @FXML
    private void initialize() {
        commentButton.setOnAction(even -> saveReview());
    }
    @FXML
    private void handleStarclick(MouseEvent event) {
        Object source = event.getSource();
        if (source == star1) selectedRating = 1;
        else if (source == star2) selectedRating = 2;
        else if (source == star3) selectedRating = 3;
        else if (source == star4) selectedRating = 4;
        else if (source == star5) selectedRating = 5;
        updateStarColors(selectedRating);
    }


    private void updateStarColors(int rating) {
        // Tạo danh sách các ngôi sao theo thứ tự
        Image selectedStar = new Image(getClass().getResourceAsStream("/image/star.png"));
        Image unselectedStar = new Image(getClass().getResourceAsStream("/image/starv.png"));
        // Cập nhật ngôi sao dựa trên rating
        if (rating >= 1) star1.setImage(selectedStar);
        else star1.setImage(unselectedStar);

        if (rating >= 2) star2.setImage(selectedStar);
        else star2.setImage(unselectedStar);

        if (rating >= 3) star3.setImage(selectedStar);
        else star3.setImage(unselectedStar);

        if (rating >= 4) star4.setImage(selectedStar);
        else star4.setImage(unselectedStar);

        if (rating >= 5) star5.setImage(selectedStar);
        else star5.setImage(unselectedStar);
    }


    public void setBook(Book book) {
        this.currentBook = book;
        if (book != null) {
            bookTitleLabel.setText(book.getTitle());
            bookAuthorLabel.setText(book.getAuthor());
            ISBNLabel.setText(book.getISBN());
            bookSectionLabel.setText(book.getSection());

            System.out.println("duong1");
            if (book.getDescription() != null) {
                System.out.println("duong2");
                System.out.println(book.getDescription());
            }
            bookDescriptionLabel.setText(book.getDescription());
            Image image = new Image(book.getImagePath()); // Đảm bảo đường dẫn hợp lệ
            bookImage.setImage(image);
            try {
                // Lấy số sao trung bình từ cơ sở dữ liệu
                double averageRating = ReviewDAO.getAverageRating(book.getISBN());
                System.out.println(averageRating);
                displayRating(averageRating);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi lấy đánh giá của sách: " + book.getTitle());
            }
        }
    }

    @FXML
    private void onBorrowBook(ActionEvent event) {
        if (currentBook != null) {
            int userId = currentUser.getId(); // Giả sử bạn có phương thức lấy userId từ currentUser
            String userName = currentUser.getUsername(); // Lấy tên người dùng từ currentUser
            int quantity = Integer.parseInt(quantityLabel.getText());
            int numberofdays = Integer.parseInt(numberOfday.getText());
            try {
                User user = User.loadStudentDetailsByID(String.valueOf(userId)); // Tạo đối tượng User mới
                boolean isBorrowed = TransactionDAO.borrowBook(user, currentBook, quantity, numberofdays);

                if (isBorrowed) {
                    System.out.println("Đã mượn sách: " + currentBook.getTitle());
                } else {
                    System.out.println("Không thể mượn sách: " + currentBook.getTitle() + " (có thể đang được mượn hoặc số lượng không đủ)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi mượn sách: " + currentBook.getTitle());
            }
        } else {
            System.out.println("Chưa chọn sách nào để mượn.");
        }
    }


    private void saveReview() {
        if (selectedRating > 0 && currentBook != null) {
            String isbn = currentBook.getISBN();
            String username = currentUser.getUsername();
            String comment = commentTextArea.getText().trim(); // Optionally fetch from a comment field if available

            try {
                ReviewDAO.saveReview(username, isbn, selectedRating, comment);
                System.out.println("Đã lưu đánh giá " + selectedRating + " sao cho sách: " + currentBook.getTitle());
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi lưu đánh giá cho sách: " + currentBook.getTitle());
            }
        } else {
            System.out.println("Chưa chọn đánh giá hoặc sách để lưu.");
        }
    }

    private void displayRating(double averageRating) {
        int roundedRating = (int) averageRating;
        Image selectedStar = new Image(getClass().getResourceAsStream("/image/star.png"));
        Image unselectedStar = new Image(getClass().getResourceAsStream("/image/starv.png"));
        Image unselectedStar1 = new Image(getClass().getResourceAsStream("/image/rating.png"));
        int i = 1;
        while (i <= roundedRating) {
            if (i == 1) starbook1.setImage(selectedStar);
            if (i == 2) starbook2.setImage(selectedStar);
            if (i == 3) starbook3.setImage(selectedStar);
            if (i == 4) starbook4.setImage(selectedStar);
            if (i == 5) starbook5.setImage(selectedStar);
            i++;
        }
        int j = roundedRating + 1;

        if (j <= 5 && (double )(roundedRating) < averageRating) {
            if (i == 1) starbook1.setImage(unselectedStar1);
            if (i == 2) starbook2.setImage(unselectedStar1);
            if (i == 3) starbook3.setImage(unselectedStar1);
            if (i == 4) starbook4.setImage(unselectedStar1);
            if (i == 5) starbook5.setImage(unselectedStar1);
        }
        j++;
        while (j <= 5) {
            if (j == 1) starbook1.setImage(unselectedStar);
            if (j == 2) starbook2.setImage(unselectedStar);
            if (j == 3) starbook3.setImage(unselectedStar);
            if (j == 4) starbook4.setImage(unselectedStar);
            if (j == 5) starbook5.setImage(unselectedStar);
            j++;
        }
    }

    private void loadReview() {
        if (currentBook != null) {
            String isbn = currentBook.getISBN();
            System.out.println(isbn);
            try {
                List<Review> reviews = ReviewDAO.getReviewsByISBN(isbn);
                displayReviews(reviews);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("L��i khi lấy đánh giá của sách: " + currentBook.getTitle());
            }
        }
        else System.out.println("null");
    }

    private void displayReviews(List<Review> reviews) {
        commentVbox.getChildren().clear();
        for (Review review : reviews) {
            // Tạo một VBox để chứa username, sao và bình luận
            VBox commentBox = new VBox(5); // 5 là khoảng cách giữa các phần tử
            commentBox.setStyle("-fx-padding: 10;"); // Tạo khoảng cách padding nếu cần

            // Label cho tên người dùng
            Label usernameLabel = new Label(review.getUsername());
            usernameLabel.setStyle("-fx-font-weight: bold;");

            // HBox cho phần sao
            HBox starsBox = new HBox(5); // Khoảng cách giữa các ngôi sao
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView();
                if (i <= review.getRating()) {
                    star.setImage(new Image(getClass().getResourceAsStream("/image/star.png")));
                } else {
                    star.setImage(new Image(getClass().getResourceAsStream("/image/starv.png")));
                }
                star.setFitWidth(15);  // Đặt chiều rộng ngôi sao là 15px
                star.setFitHeight(15); // Đặt chiều cao ngôi sao là 15px

                starsBox.getChildren().add(star);
            }

            // Label cho bình luận (dòng mới dưới ngôi sao)
            Label commentLabel = new Label(review.getComment());
            commentLabel.setWrapText(true); // Cho phép text wrap nếu bình luận dài
            commentLabel.setStyle("-fx-padding: 5px;");

            // Thêm các phần tử vào VBox
            commentBox.getChildren().addAll(usernameLabel, starsBox, commentLabel);

            // Thêm commentBox vào commentVbox (bảng chứa tất cả bình luận)
            commentVbox.getChildren().add(commentBox);
        }
    }

}
