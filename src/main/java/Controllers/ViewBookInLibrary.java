package Controllers;

import Document.Book;
import User.Member;
import User.User;
import User.currentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private AnchorPane pane;
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
        removeFocusFromAllNodes(pane);
        // Cài đặt ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        loadBooks();

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
            e.printStackTrace();
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
            }
            controller.setBook(book);

            Stage stage = new Stage();
            stage.setScene(new Scene(bookDetailsPage));
            stage.setTitle("Chi tiết sách");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi mở trang chi tiết sách");
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


    private void removeFocusFromAllNodes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            node.setFocusTraversable(false); // Không cho phép đối tượng nhận focus
            if (node instanceof Parent) {
                removeFocusFromAllNodes((Parent) node); // Đệ quy cho các node con
            }
        }
    }
}