package Controllers;

import Document.Book;
import Document.BookDAO;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;

public class ViewBooksSearchResult extends ViewBook {

    private String searchQuery; // Biến lưu truy vấn tìm kiếm

    /**
     * Tải kết quả tìm kiếm từ cơ sở dữ liệu và hiển thị chúng lên giao diện.
     * Thực hiện lazy loading khi cuộn đến cuối.
     */
    @Override
    public void loadBooks() {
        if (isLoading) return;

        isLoading = true;
        executorService.submit(() -> {
            try {
                // Lấy kết quả tìm kiếm từ cơ sở dữ liệu
                List<Book> searchResults = BookDAO.getBooksByTitle(searchQuery); // Lấy kết quả tìm kiếm

                if (searchResults != null && !searchResults.isEmpty()) {
                    int row = bookGrid.getChildren().size() / 7; // Giả sử có 7 cột
                    int col = bookGrid.getChildren().size() % 7;

                    for (Book book : searchResults) {
                        System.out.println(book.getTitle());
                        AnchorPane card = createCard(book);
                        if (card != null) {
                            int finalRow = row;
                            int finalCol = col;
                            Platform.runLater(() -> bookGrid.add(card, finalCol, finalRow));
                        }

                        col++;
                        if (col >= 7) { // Đặt số cột bạn muốn (7 ở đây)
                            col = 0;
                            row++;
                        }
                    }
                } else {
                    // Hiển thị thông báo nếu không có sách nào khớp với truy vấn
                    Platform.runLater(this::showNoResultsAlert);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                isLoading = false;
            }
        });
    }

    /**
     * Phương thức thiết lập truy vấn tìm kiếm.
     * @param searchQuery Truy vấn tìm kiếm
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery; // Lưu truy vấn tìm kiếm
        if (searchQuery != null && !searchQuery.isEmpty()) {
            loadBooks(); // Gọi phương thức để tải kết quả ngay khi truy vấn được thiết lập
        }
    }

    /**
     * Hiển thị thông báo nếu không có sách nào khớp với truy vấn.
     */
    private void showNoResultsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Không tìm thấy sách nào khớp với truy vấn.", ButtonType.OK);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}