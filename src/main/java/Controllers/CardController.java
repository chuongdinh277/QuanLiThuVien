package Controllers;

import Document.Book;
import Cache.BookCache;
import Cache.ImageCache;
import javafx.animation.ScaleTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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

        // Kiểm tra thông tin sách
        System.out.println("Đang thiết lập thông tin cho sách: " + book.getTitle());

        // Caching Book
        if (!BookCache.isCached(book.getISBN())) {
            BookCache.putBook(book);
        }

        // Cập nhật thông tin sách
        titleLabel.setText(book.getTitle());
        authorLabel.setText(book.getAuthor());
        // quantityLabel.setText(String.valueOf(book.getQuantity())); // Nếu cần sử dụng

        // Tải hình ảnh không đồng bộ
        loadImageAsync(book.getImagePath());
    }

    private void loadImageAsync(String imagePath) {
        // Hiển thị hình ảnh mặc định trước
        String defaultImagePath = "/image/defaultImage.png";
        Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
        imageView_User.setImage(defaultImage);

        // Tạo một Task để tải hình ảnh không đồng bộ
        Task<Image> imageLoadTask = new Task<Image>() {
            @Override
            protected Image call() {
                if (imagePath != null && !imagePath.isEmpty()) {
                    return ImageCache.getImage(imagePath); // Tải hình ảnh từ cache
                }
                return null; // Nếu không có đường dẫn, trả về null
            }

            @Override
            protected void succeeded() {
                // Cập nhật hình ảnh trong ImageView khi tải thành công
                Image loadedImage = getValue();
                if (loadedImage != null) {
                    imageView_User.setImage(loadedImage);
                } else {
                    System.out.println("Không thể tải hình ảnh từ: " + imagePath);
                }
            }

            @Override
            protected void failed() {
                System.out.println("Lỗi khi tải hình ảnh: " + getException().getMessage());
                getException().printStackTrace();
            }
        };

        // Khởi động Task trong một Thread mới
        new Thread(imageLoadTask).start();
    }
}