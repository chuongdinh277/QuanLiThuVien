package Controllers;

import User.User;
import Document.Book;
import User.currentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import Document.TransactionDAO;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewBookBorrowed {

    @FXML
    private ScrollPane scrollPane;  // Pane cuộn để hiển thị danh sách sách đã mượn
    @FXML
    private GridPane borrowedBookGrid;  // Lưới để chứa các thẻ sách đã mượn

    private boolean isLoading = false;  // Cờ kiểm tra nếu đang tải dữ liệu
    private boolean isAllBooksLoaded = false;  // Cờ kiểm tra nếu đã tải hết sách
    private ExecutorService executorService = Executors.newSingleThreadExecutor();  // Executor để xử lý việc tải sách trong một luồng riêng

    /**
     * Phương thức khởi tạo để cấu hình giao diện ban đầu.
     * Thiết lập các thuộc tính cho ScrollPane và bắt đầu tải sách đã mượn.
     */
    @FXML
    private void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        loadBorrowedBooks();

        // Lazy loading khi cuộn đến cuối
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isLoading && !isAllBooksLoaded && newVal.doubleValue() == 1.0) { // Kiểm tra nếu còn sách để tải
                loadBorrowedBooks();
            }
        });
    }

    /**
     * Tạo một thẻ sách từ một đối tượng Book.
     *
     * @param book Đối tượng sách để tạo thẻ
     * @return Thẻ sách dưới dạng AnchorPane
     */
    private AnchorPane createCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card.fxml"));
            AnchorPane card = loader.load();
            CardController cardController = loader.getController();
            cardController.setBook(book);
            card.setOnMouseClicked(event -> openBookDetailsPage(book));  // Mở trang chi tiết khi nhấn vào thẻ
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mở trang chi tiết của một cuốn sách khi nhấn vào thẻ sách.
     *
     * @param book Đối tượng sách cần hiển thị chi tiết
     */
    private void openBookDetailsPage(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userSeeBookDetails.fxml"));
            ScrollPane bookDetailsPage = loader.load();

            // Đưa thông tin sách vào controller của trang chi tiết
            UserSeeBookDetails bookDetailsController = loader.getController();
            bookDetailsController.setBook(book);

            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsPage));
            stage.setTitle("Chi tiết sách");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách sách đã mượn của người dùng.
     * Dữ liệu được tải trong một luồng riêng biệt để tránh làm treo giao diện người dùng.
     */
    private void loadBorrowedBooks() {
        if (isLoading || isAllBooksLoaded) return;

        isLoading = true;
        executorService.submit(() -> {
            try {
                int id = User.getStudentIdByusername(currentUser.getUsername());  // Lấy ID sinh viên từ tên đăng nhập hiện tại
                List<Book> borrowedBooks = TransactionDAO.getBorrowedBooks(String.valueOf(id));  // Lấy danh sách sách đã mượn

                if (borrowedBooks != null && !borrowedBooks.isEmpty()) {
                    int row = borrowedBookGrid.getChildren().size() / 7;
                    int col = borrowedBookGrid.getChildren().size() % 7;

                    for (Book book : borrowedBooks) {
                        AnchorPane card = createCard(book);
                        if (card != null) {
                            int finalCol = col;
                            int finalRow = row;
                            javafx.application.Platform.runLater(() -> borrowedBookGrid.add(card, finalCol, finalRow));  // Thêm thẻ vào lưới
                        }

                        col++;
                        if (col >= 7) {
                            col = 0;
                            row++;
                        }
                    }
                } else {
                    // Nếu không có sách nào
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                isLoading = false;  // Kết thúc quá trình tải
                isAllBooksLoaded = true;  // Đánh dấu là không còn sách nào để tải nữa
            }
        });
    }

}
