package controllers;

import Document.Book;
import Document.TransactionDAO;
import User.User;
import User.currentUser;
import cache.BookCache;
import cache.ImageCache;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.sql.SQLException;

public class CardController {
    @FXML
    private AnchorPane cardPane;
    @FXML
    private ImageView imageView_User;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private Button seeButton;
    @FXML
    private Button borrowButton;

    @FXML
    private Book currentBook;

    @FXML
    private void initialize() {
        // Thiết lập sự kiện chuột cho Card
        cardPane.setOnMouseEntered(this::handleMouseEnter);
        cardPane.setOnMouseExited(this::handleMouseExit);
    }

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cardPane);
        st.setToX(1.1); // Tăng kích thước chiều rộng lên 10%
        st.setToY(1.1); // Tăng kích thước chiều cao lên 10%
        st.play();
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cardPane);
        st.setToX(1.0); // Trở về kích thước ban đầu
        st.setToY(1.0); // Trở về kích thước ban đầu
        st.play();
    }
    public void setBook(Book book) {
        if (book == null) {
            System.out.println("Sách không hợp lệ!");
            return;
        }
        this.currentBook = book;

        // Kiem tra thong tin sach
        System.out.println("Đang thiết lập thông tin cho sách: " + book.getTitle());

        // Caching Book
        if (!BookCache.isCached(book.getId())) {
            BookCache.putBook(book);
        }

        titleLabel.setText(book.getTitle());
        authorLabel.setText(book.getAuthor());
//        quantityLabel.setText(String.valueOf(book.getQuantity()));

        String imagePath = book.getImagePath(); // Lấy đường dẫn hình ảnh
        System.out.println("Đường dẫn hình ảnh: " + imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            imageView_User.setImage(ImageCache.getImage(imagePath));
        } else {
            String path = "/image/biasachmacdinh.png"; // Hoặc đường dẫn tuyệt đối
            Image image = new Image(getClass().getResourceAsStream(path));

            if (image.isError()) {
                System.out.println("Không thể tải hình ảnh từ: " + path);
            } else {
                imageView_User.setImage(image);
            }
        }
    }

    @FXML
    private void onSeeBook() {
        // Logic để xem chi tiết sách
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

    @FXML
    public void onReturnBook(ActionEvent event) {
        if (currentBook != null) {
            int userId = currentUser.getId(); // Assume you have a method to get userId from currentUser
            String userName = currentUser.getUsername(); // Get the username from currentUser

            try {
                User user = new User(userId, userName); // Create a new User object
                boolean isReturned = TransactionDAO.returnBook(user, currentBook);

                if (isReturned) {
                    System.out.println("Returned book: " + currentBook.getTitle());
                } else {
                    System.out.println("Unable to return book: " + currentBook.getTitle() + " (it may not be borrowed or already returned)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error returning book: " + currentBook.getTitle());
            }
        } else {
            System.out.println("No book selected to return.");
        }
    }
}