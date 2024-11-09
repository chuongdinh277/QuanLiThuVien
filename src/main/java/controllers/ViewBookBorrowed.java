package controllers;
import User.User;
import Document.Book;
import User.currentUser;
import javafx.concurrent.Task;
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

public class ViewBookBorrowed {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane borrowedBookGrid;

    private boolean isLoading = false;
    private boolean isAllBooksLoaded = false; // Cờ để kiểm tra nếu đã tải hết sách
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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

    private AnchorPane createCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/card1.fxml"));
            AnchorPane card = loader.load();
            CardController cardController = loader.getController();
            cardController.setBook(book);
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải card");
            return null;
        }
    }

    private void loadBorrowedBooks() {
        if (isLoading || isAllBooksLoaded) return;

        isLoading = true;
        executorService.submit(() -> {
            try {
                     //int id = User.getStudentIdByusername(currentUser.getUsername());

                List<Book> borrowedBooks = TransactionDAO.getBorrowedBooks(currentUser.getUsername());

                if (borrowedBooks != null && !borrowedBooks.isEmpty()) {
                    int row = borrowedBookGrid.getChildren().size() / 6;
                    int col = borrowedBookGrid.getChildren().size() % 6;

                    for (Book book : borrowedBooks) {
                        AnchorPane card = createCard(book);
                        if (card != null) {
                            int finalRow = row;
                            int finalCol = col;
                            javafx.application.Platform.runLater(() -> borrowedBookGrid.add(card, finalCol, finalRow));
                        }
                        col++;
                        if (col >= 6) {
                            col = 0;
                            row++;
                        }
                    }
                } else {
                    System.out.println("Không tìm thấy sách nào đã mượn.");
                    isAllBooksLoaded = true; // Đánh dấu rằng đã tải hết sách
                }
            } finally {
                isLoading = false;
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
