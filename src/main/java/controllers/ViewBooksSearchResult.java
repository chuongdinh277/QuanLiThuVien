package controllers;

import Document.Book;
import Document.BookDAO;
import User.currentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import Document.TransactionDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewBooksSearchResult {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane searchResultGrid; // The GridPane to hold search result book cards

    private boolean isLoading = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String searchQuery; // Biến để lưu truy vấn tìm kiếm

    @FXML
    private void initialize() {
        // Cài đặt ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Tải kết quả tìm kiếm khi khởi động nếu có truy vấn
        if (searchQuery != null && !searchQuery.isEmpty()) {
            loadSearchResults();
        }

        // Lazy loading khi cuộn đến cuối
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isLoading && newVal.doubleValue() == 1.0) {
                loadSearchResults(); // Tải thêm kết quả tìm kiếm khi cuộn đến cuối
            }
        });
    }

    private AnchorPane createCard(Book book) {
        try {
            // Tạo một FXMLLoader mới mỗi lần
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card1.fxml"));
            AnchorPane card = loader.load(); // Tải một card mới

            CardController cardController = loader.getController();
            cardController.setBook(book); // Giả sử bạn đã có phương thức này trong CardController

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải card");
            return null;
        }
    }

    private void loadSearchResults() {
        if (isLoading) return;

        isLoading = true;
        executorService.submit(() -> {
            try {
                //List<Book>
                try {
                    List<Book> searchResults = BookDAO.getBooksByTitle(searchQuery); // Fetch search results


                    if (searchResults != null && !searchResults.isEmpty()) {
                        int row = searchResultGrid.getChildren().size() / 5; // Giả sử có 5 cột
                        int col = searchResultGrid.getChildren().size() % 5;

                        for (Book book : searchResults) {
                            AnchorPane card = createCard(book);
                            if (card != null) {
                                int finalRow = row;
                                int finalCol = col;
                                javafx.application.Platform.runLater(() -> searchResultGrid.add(card, finalCol, finalRow));
                            }

                            col++;
                            if (col >= 5) { // Set the number of columns you want (5 in this case)
                                col = 0;
                                row++;
                            }
                        }
                    } else {
                        System.out.println("Không tìm thấy sách nào khớp với truy vấn.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } finally {
                isLoading = false;
            }
        });
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery; // Lưu truy vấn tìm kiếm
        loadSearchResults(); // Gọi phương thức để tải kết quả ngay khi truy vấn được thiết lập
    }

    public void shutdown() {
        executorService.shutdown();
    }
}