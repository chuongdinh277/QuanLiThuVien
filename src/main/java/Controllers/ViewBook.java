package Controllers;

import User.User;
import Document.Book;
import User.currentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

public abstract class ViewBook {
    @FXML
    protected AnchorPane pane;
    @FXML
    protected ScrollPane scrollPane;
    @FXML
    protected GridPane bookGrid;
    protected int booksLoaded = 0;
    protected static final int PAGE_SIZE = 21;

    protected boolean isLoading = false;
    protected ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Phương thức trừu tượng để các lớp con cài đặt
    public abstract void loadBooks();

    /**
     * Phương thức khởi tạo để cấu hình giao diện ban đầu.
     * Thiết lập các thuộc tính cho ScrollPane và bắt đầu tải sách đã mượn.
     */
    @FXML
    private void initialize() {
        setupScrollPaneScroll(scrollPane);
        removeFocusFromAllNodes(pane);
        // Cài đặt ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        loadBooks(); // Gọi phương thức loadBooks của lớp con

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isLoading && newVal.doubleValue() == 1.0) {
                loadBooks(); // Gọi lại loadBooks khi cuộn xuống cuối
            }
        });
    }

    /**
     * Tạo một thẻ sách từ một đối tượng Book.
     *
     * @param book Đối tượng sách để tạo thẻ
     * @return Thẻ sách dưới dạng AnchorPane
     */
    protected AnchorPane createCard(Book book) {
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
            return null;
        }
    }

    /**
     * Tạo một thẻ sách từ một đối tượng Book.
     *
     * @return Thẻ sách dưới dạng AnchorPane
     */
    private void removeFocusFromAllNodes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            node.setFocusTraversable(false); // Không cho phép đối tượng nhận focus
            if (node instanceof Parent) {
                removeFocusFromAllNodes((Parent) node); // Đệ quy cho các node con
            }
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
            UserSeeBookDetails controller = loader.getController();
            controller.setBook(book); // Gọi phương thức setBook

            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsPage));
            stage.setTitle("Chi tiết sách");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi mở trang chi tiết sách");
        }
    }

    /**
     * Tăng tốc độ cuộn chuột
     *
     */
    private void setupScrollPaneScroll(ScrollPane scrollPane) {
        double verticalScrollSpeedFactor = 3.0; // Tăng tốc độ cuộn đứng

        scrollPane.setOnScroll(event -> {
            // Cuộn đứng với tốc độ gấp 3 lần
            scrollPane.setVvalue(scrollPane.getVvalue() - event.getDeltaY() * verticalScrollSpeedFactor / scrollPane.getContent().getBoundsInLocal().getHeight());

            event.consume(); // Ngăn chặn sự kiện cuộn mặc định
        });
    }
}