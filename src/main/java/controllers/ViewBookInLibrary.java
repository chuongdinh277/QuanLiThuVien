package controllers;

import Document.Book;
import User.Member;
import User.User;
import User.currentUser;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewBookInLibrary {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane bookGrid;

    private int booksLoaded = 0;
    private static final int PAGE_SIZE = 21;

    private boolean isLoading = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    private void initialize() {
        // Cài đặt ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        loadBooks();

        // Lazy loading khi cuộn đến cuối
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isLoading && newVal.doubleValue() == 1.0) {
                loadBooks();
            }
        });
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
            showErrorDialog("Error", "Lỗi tải card" + e.getMessage());
            return null;
        }
    }

    private void openBookDetailsPage(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userSeeBookDetails.fxml"));
            AnchorPane bookDetailsPage = loader.load();

            // Đưa thông tin sách vào controller của trang chi tiết
            UserSeeBookDetails bookDetailsController = loader.getController();
            bookDetailsController.setBook(book);

            // Hiển thị trang chi tiết (ví dụ, trong một cửa sổ mới)
            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsPage));
            stage.setTitle("Chi tiết sách");
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Lỗi khi mở trang chi tiết sách" + e.getMessage());
        }
    }
    private void loadBooks() {
        if (isLoading) return;

        isLoading = true;
        executorService.submit(() -> {
            try {
                int studentID = User.getStudentIdByusername(currentUser.getUsername());
                Member member = new Member(studentID,currentUser.getUsername(), currentUser.getRole());
                List<Book> bookList = member.viewBooksPaginated(booksLoaded, PAGE_SIZE);
                if (bookList != null && !bookList.isEmpty()) {
                    int row = booksLoaded / 6;
                    int col = booksLoaded % 6;

                    for (Book book : bookList) {
                        AnchorPane card = createCard(book);
                        if (card != null) {
                            int finalRow = row;
                            int finalCol = col;
                            javafx.application.Platform.runLater(() -> bookGrid.add(card, finalCol, finalRow));
                        }

                        col++;
                        if (col >= 6) {
                            col = 0;
                            row++;
                        }
                    }
                    booksLoaded += bookList.size();
                } else {
                    showAlbertDialog("Không có sách mới để tải");
                }
            } catch (SQLException e) {
                showErrorDialog("Title", "Lỗi khi tải sách" + e.getMessage());
            } finally {
                isLoading = false;
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
    private void showErrorDialog(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
    }
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}