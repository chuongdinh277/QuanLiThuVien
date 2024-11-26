package controllers;

import java.sql.SQLException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Document.*;
import APIGoogle.*;

public class addBook {

    @FXML
    private TextField titleSearch;
    @FXML
    private TextField authorSearch;
    @FXML
    private Button apiSearch;
    @FXML
    private TextField titleSearch_1;
    @FXML
    private TextField authorSearch_1;
    @FXML
    private TextField categorySearch_1;
    @FXML
    private Button addBook_search;
    @FXML
    private ImageView showImage;
    @FXML
    private TextField quantity_book;
    @FXML
    private TextField bookPublisher;
    @FXML
    private TextField bookSection;
    @FXML
    private TextField ISBNtextField;

    private String description;
    private Book resultBook;  // Lưu kết quả tìm kiếm
    private homeController_Admin homeController;

    public void setHomeController(homeController_Admin homeController) {
        this.homeController = homeController;
    }

    @FXML
    private void apiSearch_Button() {
        apiSearch.setOnAction(e -> {
            String title = titleSearch.getText().trim();
            String author = authorSearch.getText().trim();

            if (!title.isEmpty() && !author.isEmpty()) {
                // Tạo một Task để tìm kiếm sách qua API trong background thread
                Task<Book> searchTask = new Task<>() {
                    @Override
                    protected Book call() {
                        String bookInfo = GoogleBooksAPI.searchBookByTitleAndAuthor(title, author);
                        return BookParser.parseBookInfo(bookInfo);
                    }

                    @Override
                    protected void succeeded() {
                        resultBook = getValue();
                        if (resultBook != null) {
                            Platform.runLater(() -> {
                                titleSearch_1.setText(resultBook.getTitle());
                                authorSearch_1.setText(resultBook.getAuthor());
                                categorySearch_1.setText(resultBook.getCategory());
                                ISBNtextField.setText(resultBook.getISBN());
                                description = resultBook.getDescription() != null ? resultBook.getDescription() : "No description available";

                                if (resultBook.getImagePath() != null) {
                                    showImage.setImage(new Image(resultBook.getImagePath()));
                                } else {
                                    showImage.setImage(null);
                                }
                            });
                        } else {
                            showErrorDialog("No book found for the given title and author.");
                        }
                    }

                    @Override
                    protected void failed() {
                        showErrorDialog("Error occurred while searching for the book.");
                    }
                };

                new Thread(searchTask).start(); // Chạy task trên luồng nền
            } else {
                showErrorDialog("Please enter both title and author.");
            }
        });
    }

    @FXML
    private void addBook_Button() {
        addBook_search.setOnAction(e -> {
            String title = titleSearch_1.getText().trim();
            String author = authorSearch_1.getText().trim();
            String category = categorySearch_1.getText().trim();
            String quantityStr = quantity_book.getText().trim();
            String publisher = bookPublisher.getText().trim();
            String section = bookSection.getText().trim();
            String ISBN = ISBNtextField.getText().trim();

            // Kiểm tra và chuyển đổi số lượng sách
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid quantity input. Please enter a valid number.");
                return;
            }

            // Lấy đường dẫn hình ảnh
            String imagePath = showImage.getImage() != null ? showImage.getImage().getUrl() : null;

            // Tạo đối tượng Book và thêm vào cơ sở dữ liệu
            Book book = new Book(title, author, category, quantity, quantity, description, publisher, section, imagePath, ISBN);

            try {
                BookDAO.addBook(book); // Thêm sách vào cơ sở dữ liệu
                showSuccessDialog("Book added successfully!");
                clearInputFields(); // Làm sạch các trường sau khi thêm
            } catch (SQLException ex) {
                showErrorDialog("Failed to add book to the database: " + ex.getMessage());
            }
        });
    }

    // Hàm để làm sạch các trường input
    private void clearInputFields() {
        titleSearch_1.clear();
        authorSearch_1.clear();
        categorySearch_1.clear();
        quantity_book.clear();
        bookPublisher.clear();
        bookSection.clear();
        ISBNtextField.clear();
        showImage.setImage(null);
    }

    // Hiển thị hộp thoại lỗi
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Hiển thị hộp thoại thành công
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
