package Controllers;

import Document.Book;
import Document.BookDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserHomeController {

    @FXML
    private GridPane popularBookGrid;

    @FXML
    private ScrollPane popularBookPane;

    @FXML
    private GridPane recentlyBookGrid;

    @FXML
    private ScrollPane recentlyBookPane;

    @FXML
    private AnchorPane pane;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Phương thức khởi tạo, dùng để loại bỏ focus của các node trong giao diện và tải sách.
     */
    @FXML
    private void initialize() {
        removeFocusFromAllNodes(pane);

        loadBooks();
    }

    /**
     * Tải sách phổ biến và sách mới từ cơ sở dữ liệu và hiển thị lên giao diện.
     */
    private void loadBooks() {
        executorService.submit(() -> {
            List<Book> popularBooks = null;
            try {
                // Lấy danh sách sách phổ biến từ cơ sở dữ liệu
                popularBooks = BookDAO.getBooksByAverageRating();
                System.out.println("Số sách phổ biến: " + (popularBooks != null ? popularBooks.size() : 0));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Book> finalPopularBooks = popularBooks;
            Platform.runLater(() -> {
                popularBookGrid.getChildren().clear();  // Xóa các thẻ sách cũ
                if (finalPopularBooks != null && !finalPopularBooks.isEmpty()) {
                    System.out.println("popular");
                    int col = 0;  // Bắt đầu từ cột 0
                    for (Book book : finalPopularBooks) {
                        popularBookGrid.add(createCard1(book), col, 0);  // Thêm thẻ sách vào hàng 0
                        col++;
                        if (col >= 5) {  // Giới hạn số cột là 5
                            break;  // Dừng lại nếu đã đủ số cột
                        }
                    }
                }
                popularBookPane.setVvalue(1.0);  // Cuộn xuống cuối
            });

            List<Book> recentlyBooks = null;
            try {
                recentlyBooks = BookDAO.getRecentlyAddedBooks();
                System.out.println("Số sách mới: " + (recentlyBooks != null ? recentlyBooks.size() : 0));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Book> finalRecentlyBooks = recentlyBooks;
            Platform.runLater(() -> {
                recentlyBookGrid.getChildren().clear();
                if (finalRecentlyBooks != null && !finalRecentlyBooks.isEmpty()) {
                    System.out.println("recent");
                    int col = 0;
                    for (Book book : finalRecentlyBooks) {
                        recentlyBookGrid.add(createCard(book), col, 0);
                        col++;
                        if (col >= 7) {
                            break;
                        }
                    }
                }
                recentlyBookPane.setVvalue(1.0);
            });
        });
    }

    /**
     * Tạo thẻ sách với thông tin từ đối tượng sách sử dụng card1.fxml.
     *
     * @param book Đối tượng sách
     * @return Thẻ sách được tạo
     */
    private AnchorPane createCard1(Book book) {
        try {
            // Tạo một FXMLLoader mới mỗi lần
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card1.fxml"));
            AnchorPane card = loader.load();  // Tải thẻ sách mới

            CardController cardController = loader.getController();
            cardController.setBook(book);  // Đặt thông tin sách cho card
            card.setOnMouseClicked(event -> openBookDetailsPage(book));  // Mở trang chi tiết sách khi click vào thẻ

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tạo thẻ sách với thông tin từ đối tượng sách sử dụng card.fxml.
     *
     * @param book Đối tượng sách
     * @return Thẻ sách được tạo
     */
    private AnchorPane createCard(Book book) {
        try {
            // Tạo một FXMLLoader mới mỗi lần
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card.fxml"));
            AnchorPane card = loader.load();

            CardController cardController = loader.getController();
            cardController.setBook(book);
            card.setOnMouseClicked(event -> openBookDetailsPage(book));

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mở trang chi tiết sách khi người dùng click vào thẻ sách.
     *
     * @param book Đối tượng sách cần xem chi tiết
     */
    private void openBookDetailsPage(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userSeeBookDetails.fxml"));
            ScrollPane bookDetailsPage = loader.load();

            UserSeeBookDetails controller = loader.getController();
            controller.setBook(book);

            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsPage));
            stage.setTitle("Chi tiết sách");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loại bỏ focus của tất cả các node trong parent.
     *
     * @param parent Parent chứa các node cần loại bỏ focus
     */
    private void removeFocusFromAllNodes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            node.setFocusTraversable(false);
            if (node instanceof Parent) {
                removeFocusFromAllNodes((Parent) node);
            }
        }
    }
}
