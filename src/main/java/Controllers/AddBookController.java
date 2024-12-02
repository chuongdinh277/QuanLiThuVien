package Controllers;

import java.sql.SQLException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Document.*;
import APIGoogle.*;

/**
 * Controller để thêm sách mới vào hệ thống.
 */
public class AddBookController {

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
    private TextArea descriptionTextArea;
    @FXML
    private TextField ISBNtextField;

    private String description;
    private Book resultBook;
    private AllBookController homeController;

    /**
     * Thiết lập controller chính để điều hướng chi tiết sách.
     *
     * @param homeController Đối tượng controller chính.
     */
    public void setHomeController(AllBookController homeController) {
        this.homeController = homeController;
    }

    /**
     * Tìm kiếm sách bằng API dựa trên tiêu đề và tác giả được cung cấp.
     */
    @FXML
    private void apiSearch_Button() {
        apiSearch.setOnAction(e -> {
            String title = titleSearch.getText().trim();
            String author = authorSearch.getText().trim();

            if (!title.isEmpty() && !author.isEmpty()) {
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
                                bookPublisher.setText(resultBook.getPublisher());
                                ISBNtextField.setText(resultBook.getISBN());
                                description = resultBook.getDescription() != null ? resultBook.getDescription() : "Không có mô tả";
                                descriptionTextArea.setText(description);
                                if (resultBook.getImagePath() != null) {
                                    Image image = new Image(resultBook.getImagePath(), showImage.getFitWidth(), showImage.getFitHeight(), true, true);
                                    showImage.setImage(image);
                                    showImage.setSmooth(true);
                                } else {
                                    showImage.setImage(null);
                                }
                            });
                        } else {
                            showErrorDialog("Không tìm thấy sách với tiêu đề và tác giả đã cung cấp.");
                        }
                    }

                    @Override
                    protected void failed() {
                        showErrorDialog("Đã xảy ra lỗi khi tìm kiếm sách.");
                    }
                };

                new Thread(searchTask).start();
            } else {
                showErrorDialog("Vui lòng nhập cả tiêu đề và tác giả.");
            }
        });
    }

    /**
     * Thêm sách mới vào hệ thống sau khi kiểm tra dữ liệu nhập vào.
     */
    @FXML
    private void addBook_Button() {
        addBook_search.setOnAction(e -> {
            String title = titleSearch_1.getText().trim();
            String author = authorSearch_1.getText().trim();
            String category = categorySearch_1.getText().trim();
            String quantityStr = quantity_book.getText().trim();
            String publisher = bookPublisher.getText().trim();
            String ISBN = ISBNtextField.getText().trim();

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException ex) {
                showErrorDialog("Số lượng không hợp lệ. Vui lòng nhập số hợp lệ.");
                return;
            }

            String imagePath = showImage.getImage() != null ? showImage.getImage().getUrl() : null;

            Book book = new Book(title, author, category, quantity, quantity, description, publisher, imagePath, ISBN);

            try {
                BookDAO.addBook(book);
                showSuccessDialog("Thêm sách thành công!");
                clearInputFields();
            } catch (SQLException ex) {
                showErrorDialog("Không thể thêm sách vào cơ sở dữ liệu: " + ex.getMessage());
            }
        });
    }

    /**
     * Xóa tất cả dữ liệu nhập trên form.
     */
    private void clearInputFields() {
        titleSearch_1.clear();
        authorSearch_1.clear();
        categorySearch_1.clear();
        quantity_book.clear();
        bookPublisher.clear();
        descriptionTextArea.clear();
        ISBNtextField.clear();
        showImage.setImage(null);
    }

    /**
     * Hiển thị hộp thoại lỗi với thông báo đã cho.
     *
     * @param message Thông báo hiển thị trong hộp thoại lỗi.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thành công với thông báo đã cho.
     *
     * @param message Thông báo hiển thị trong hộp thoại thành công.
     */
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
