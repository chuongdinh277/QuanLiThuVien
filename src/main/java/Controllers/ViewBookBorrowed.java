package Controllers;

import User.User;
import Document.Book;
import User.currentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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


public class ViewBookBorrowed extends ViewBook {
    private boolean isAllBooksLoaded = false; // Cờ kiểm tra nếu đã tải hết sách


    /**
     * Tải danh sách sách đã mượn của người dùng.
     * Dữ liệu được tải trong một luồng riêng biệt để tránh làm treo giao diện người dùng.
     */
    @Override
    public void loadBooks() {
        if (isLoading) return;

        isLoading = true;
        executorService.submit(() -> {
            try {
                int id = User.getStudentIdByusername(currentUser.getUsername());  // Lấy ID sinh viên từ tên đăng nhập hiện tại
                List<Book> bookList = TransactionDAO.getBorrowedBooks(String.valueOf(id));  // Lấy danh sách sách đã mượn

                if (bookList == null || bookList.isEmpty()) {
                    // Hiển thị thông báo nếu không có sách nào đã mượn
                    javafx.application.Platform.runLater(() -> showAlert("Chưa có sách nào được mượn", "Bạn chưa mượn sách nào."));
                } else {
                    int row = booksLoaded / 7;
                    int col = booksLoaded % 7;

                    for (Book book : bookList) {
                        AnchorPane card = createCard(book);
                        if (card != null) {
                            int finalRow = row;
                            int finalCol = col;
                            javafx.application.Platform.runLater(() -> bookGrid.add(card, finalCol, finalRow));
                        }

                        col++;
                        if (col >= 7) {
                            col = 0;
                            row++;
                        }
                    }
                    booksLoaded += bookList.size();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                isLoading = false;
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
