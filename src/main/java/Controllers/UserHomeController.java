package Controllers;

import BookRating.BookRating;
import Document.Book;
import Document.BookDAO;
import Recomend.Recommend;
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

    @FXML
    private void initialize() {
        removeFocusFromAllNodes(pane);

        loadBooks();

        // Lazy loading khi cuộn đến cuối
    }

    private void loadBooks() {
        executorService.submit(() -> {
            List<Book> popularBooks = null;
            try {
                popularBooks = BookDAO.getBooksByAverageRating();
                System.out.println("Số sách phổ biến: " + (popularBooks != null ? popularBooks.size() : 0));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Book> finalPopularBooks = popularBooks;
            Platform.runLater(() -> {
                popularBookGrid.getChildren().clear(); // Xóa các card trước đó
                if (finalPopularBooks != null && !finalPopularBooks.isEmpty()) {
                    System.out.println("popular");
                    int col = 0; // Bắt đầu từ cột 0
                    for (Book book : finalPopularBooks) {
                        popularBookGrid.add(createCard1(book), col, 0); // Thêm vào hàng 0
                        col++;
                        if (col >= 7) { // Giới hạn số cột là 7
                            break; // Dừng lại nếu đã đủ 7 cột
                        }
                    }
                }
                popularBookPane.setVvalue(1.0); // Cuộn xuống cuối
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
                recentlyBookGrid.getChildren().clear(); // Xóa các card trước đó
                if (finalRecentlyBooks != null && !finalRecentlyBooks.isEmpty()) {
                    System.out.println("recent");
                    int col = 0; // Bắt đầu từ cột 0
                    for (Book book : finalRecentlyBooks) {
                        recentlyBookGrid.add(createCard(book), col, 0); // Thêm vào hàng 0
                        col++;
                        if (col >= 7) { // Giới hạn số cột là 7
                            break; // Dừng lại nếu đã đủ 7 cột
                        }
                    }
                }
                recentlyBookPane.setVvalue(1.0); // Cuộn xuống cuối
            });
        });
    }

    private AnchorPane createCard1(Book book) {
        try {
            // Tạo một FXMLLoader mới mỗi lần
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card1.fxml"));
            AnchorPane card = loader.load(); // Tải một card mới

            CardController cardController = loader.getController();
            cardController.setBook(book);
            card.setOnMouseClicked(event -> openBookDetailsPage(book));

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải card");
            return null;
        }
    }
    private AnchorPane createCard(Book book) {
        try {
            // Tạo một FXMLLoader mới mỗi lần
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card.fxml"));
            AnchorPane card = loader.load(); // Tải một card mới

            CardController cardController = loader.getController();
            cardController.setBook(book);
            card.setOnMouseClicked(event -> openBookDetailsPage(book));

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải card");
            return null;
        }
    }

    private void openBookDetailsPage(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userSeeBookDetails.fxml"));
            ScrollPane bookDetailsPage = loader.load();

            // Đưa thông tin sách vào controller của trang chi tiết
            UserSeeBookDetails controller = loader.getController();
            if (book == null) {
                System.out.println("null");
            }
            controller.setBook(book);


            // Hiển thị trang chi tiết (ví dụ, trong một cửa sổ mới)
            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsPage));
            stage.setTitle("Chi tiết sách");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi mở trang chi tiết sách");
        }
    }

    private void removeFocusFromAllNodes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            node.setFocusTraversable(false); // Không cho phép đối tượng nhận focus
            if (node instanceof Parent) {
                removeFocusFromAllNodes((Parent) node); // Đệ quy cho các node con
            }
        }
    }

}
