package controllers;

import java.sql.SQLException;
import Document.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import APIGoogle.*;
import Document.BookDAO;

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

    private homeController_Admin homeController;

    public void setHomeController(homeController_Admin homeController) {
        this.homeController = homeController;
    }
    @FXML
    private void apiSearch_Button() {
        apiSearch.setOnAction(e -> {
            String title = titleSearch.getText();
            String author = authorSearch.getText();

            // Tìm kiếm sách qua API
            String bookInfo = GoogleBooksAPI.searchBookByTitleAndAuthor(title, author);

            if (bookInfo != null) {
                // Phân tích thông tin sách từ API và điền vào các trường trong form
                Book book = BookParser.parseBookInfo(bookInfo);

                titleSearch_1.setText(book.getTitle());
                authorSearch_1.setText(book.getAuthor());
                categorySearch_1.setText(book.getCategory());
                ISBNtextField.setText(book.getISBN());

                if (book.getImagePath() != null) {
                    Image image = new Image(book.getImagePath());
                    showImage.setImage(image);
                }
            } else {
                showErrorDialog("No book found for the given title and author.");
            }
        });
    }

    @FXML
    private void addBook_Button() {
        addBook_search.setOnAction(e -> {
            String title = titleSearch_1.getText();
            String author = authorSearch_1.getText();
            String category = categorySearch_1.getText();
            String quantity = quantity_book.getText();
            String publisher = bookPublisher.getText();
            String section = bookSection.getText();
            String ISBN = ISBNtextField.getText();

            // Kiểm tra số lượng sách
            int quantity_Book = 0;
            try {
                quantity_Book = Integer.parseInt(quantity);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid quantity input. Please enter a valid number.");
                return;
            }

            // Lấy đường dẫn hình ảnh
            String imagePath = showImage.getImage() != null ? showImage.getImage().getUrl().toString() : null;

            // Tạo đối tượng sách và thêm vào cơ sở dữ liệu
            Book book = new Book(title, author, category, quantity_Book, quantity_Book, "description", publisher, section, imagePath, ISBN);

            try {
                BookDAO.addBook(book); // Thêm sách vào cơ sở dữ liệu
                showSuccessDialog("Book added successfully!");
                clearInputFields(); // Làm sạch các trường dữ liệu sau khi thêm
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