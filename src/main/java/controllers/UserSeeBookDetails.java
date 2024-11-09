package controllers;

import Document.Book;
import Document.TransactionDAO;
import User.User;
import User.currentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

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
    private Label bookPublisherLabel;

    @FXML
    private Label bookDescriptionLabel;

    @FXML
    private Book currentBook;

    public void setBook(Book book) {
        this.currentBook = book;
        if (book != null) {
            bookTitleLabel.setText(book.getTitle());
            bookAuthorLabel.setText(book.getAuthor());
            System.out.println("duong1");
            if (book.getDescription() != null) {
                System.out.println("duong2");
                System.out.println(book.getDescription());
            }
            bookDescriptionLabel.setText(book.getDescription());
            Image image = new Image(book.getImagePath()); // Đảm bảo đường dẫn hợp lệ
            bookImage.setImage(image);
        }
    }

    @FXML
    private void onBorrowBook(ActionEvent event) {
        if (currentBook != null) {
            int userId = currentUser.getId(); // Giả sử bạn có phương thức lấy userId từ currentUser
            String userName = currentUser.getUsername(); // Lấy tên người dùng từ currentUser

            try {
                User user = new User(userId, userName); // Tạo đối tượng User mới
                boolean isBorrowed = TransactionDAO.borrowBook(user, currentBook, 1, 100);

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
}
