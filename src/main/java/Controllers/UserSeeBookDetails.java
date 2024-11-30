package Controllers;

import Document.*;
import User.User;
import User.currentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class UserSeeBookDetails {


    @FXML
    private Label ISBNLabel;

    @FXML
    private ImageView ImageView_book;

    @FXML
    private TextField authorTextField;

    @FXML
    private Label availableLabel;

    @FXML
    private TableColumn<Transaction, Date> borrowDate;

    @FXML
    private Label categoryLabel;

    @FXML
    private Button commentBook;

    @FXML
    private Button commentButton;

    @FXML
    private Pane commentPane;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private VBox commentVbox;

    @FXML
    private TextArea descriptionTextField;

    @FXML
    private Label publisherLabel;

    @FXML
    private TextField quantityTextField;

    @FXML
    private TableColumn<Transaction, Date> returnDate;

    @FXML
    private ImageView returnHome;

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
    private Button borrowBook;
    @FXML
    private Button returnBook;

    @FXML
    private TextField titleTextField;

    @FXML
    private Button viewBook;

    @FXML
    private Pane viewBookPane;
    @FXML
    private TextField bookIDtextField;
    public Book currentBook;

    private MenuController_Admin menuControllerAdmin;
    @FXML
    private TextField sectionTextField;
    private int selectedRating = 0;
    @FXML
    private Button exit;

    @FXML
    private void exit(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }



    @FXML
    private void returnBook(ActionEvent event) {
        try {
            String mssv = String.valueOf(currentUser.getId());
            Transaction transaction = TransactionDAO.getTransactionByISBNAndMssv(currentBook.getISBN(), mssv);
            boolean success = TransactionDAO.deleteTransaction(transaction.getId());
            int quantity = currentBook.getQuantity();
            boolean check = BookDAO.updateRemainingByISBN(currentBook.getISBN(), quantity);
            if (success && check) {
                showAlbertDialog("Trả sách thành công");
                borrowBook.setVisible(true);
                returnBook.setVisible(false);
                //menuControllerAdmin.loadBookList();
            } else {
                showAlbertDialog("Trả thất bại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void borrowBook(ActionEvent event) {
        try {
            String mssv = String.valueOf(currentUser.getId());
            System.out.println(mssv);
            User newUser = User.loadStudentDetailsByID(mssv);
            boolean isBorrow = TransactionDAO.borrowBook(newUser, currentBook, 1,10);
            if (isBorrow) {
                borrowBook.setVisible(false);
                returnBook.setVisible(true);
                showAlbertDialog("Mượn sách thành công");
                //menuControllerAdmin.loadBookList();
            } else {
                showAlbertDialog("Mượn thất bại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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
    private void handleClick(MouseEvent event) {
        if (menuControllerAdmin != null) {
            menuControllerAdmin.showHome();
        }
        else {
            System.out.println("null");
        }
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
    public void setMenuController(MenuController_Admin menuController) {
        this.menuControllerAdmin = menuController;
    }

    public void setBook (Book book) {
        this.currentBook = book;
        if (book != null) {
            System.out.println(currentBook.getISBN());
            //System.out.println(currentBook.getISBN());
            ISBNLabel.setText(book.getISBN());
            titleTextField.setText(book.getTitle());
            authorTextField.setText(book.getAuthor());
            categoryLabel.setText(book.getCategory());
            publisherLabel.setText(book.getPublisher());
            quantityTextField.setText(String.valueOf(book.getQuantity()));
            descriptionTextField.setText(book.getDescription());
            if (book.getRemainingBook() <=0) {
                availableLabel.setText("Available");
            }
            else {
                availableLabel.setText("Not available");
            }

            String mssv = String.valueOf(currentUser.getId());
            // User newUser = User.loadStudentDetailsByID(mssv);
            boolean isBorrowed = TransactionDAO.getBorrowedBooksByMssv(mssv, currentBook);
            // Nếu đã mượn sách, hiển thị nút "Return Book", ngược lại là "Borrow Book"
            if (isBorrowed) {
                borrowBook.setVisible(false);
                returnBook.setVisible(true);
            } else {
                borrowBook.setVisible(true);
                returnBook.setVisible(false);
            }

            try {
                // Lấy số sao trung bình từ cơ sở dữ liệu
                double averageRating = ReviewDAO.getAverageRating(book.getISBN());
                System.out.println(averageRating);
                displayRating(averageRating);
            } catch (SQLException e) {
                showErrorDialog("Lỗi khi lấy đánh giá của sách: " + book.getTitle());
            }
        }
        if (book.getImagePath() != null) {
            ImageView_book.setImage(new Image(book.getImagePath()));
        }
    }
    @FXML
    private void saveReview() {
        if (selectedRating > 0 && currentBook != null) {
            String isbn = currentBook.getISBN();
            String username = currentUser.getUsername();
            String comment = commentTextArea.getText().trim(); // Optionally fetch from a comment field if available

            try {
                ReviewDAO.saveReview(username, isbn, selectedRating, comment);
                showAlbertDialog("Đã lưu đánh giá " + selectedRating + " sao cho sách: " + currentBook.getTitle());
            } catch (SQLException e) {
                showErrorDialog("Lỗi khi lưu đánh giá cho sách: " + currentBook.getTitle());
            }
        } else {
            showAlbertDialog("Chưa chọn đánh giá hoặc sách để lưu.");
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
                showErrorDialog("Lỗi khi lấy đánh giá của sách: " + currentBook.getTitle());
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

    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
