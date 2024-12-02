package Controllers;

import Document.Book;
import User.Admin;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

import javafx.scene.chart.BarChart;

public class AllBookController {

    @FXML
    private BorderPane borderPane_book;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> bookID;
    @FXML
    private TableColumn<Book, String> bookTitle;
    @FXML
    private TableColumn<Book, String> bookAuthor;
    @FXML
    private TableColumn<Book, String> bookPublisher;
    @FXML
    private TableColumn<Book, Integer> bookQuantity;
    @FXML
    private TableColumn<Book, HBox> bookAction;
    @FXML
    private TableColumn<Book, String> bookCategory;
    @FXML
    private TableColumn<Book, Integer> remainingBook;
    @FXML
    private TableColumn<Book, String> bookAvailability;
    @FXML
    private TableColumn<Book, String> ISBNbook;
    @FXML
    private Button addBook_Admin;
    @FXML
    private BarChart<String, Number> categoryBarChart;
    private static MenuAdminController menuController;

    /**
     * Thiết lập MenuController cho controller này.
     * @param menuController MenuController_Admin cần thiết lập.
     */
    public void setMenuController(MenuAdminController menuController) {
        this.menuController = menuController;
    }

    /**
     * Khởi tạo controller bằng cách thiết lập TableView và tải danh sách sách.
     */
    @FXML
    private void initialize() {
        ISBNbook.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getISBN()));
        bookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bookAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        bookPublisher.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPublisher()));
        bookQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        bookCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        remainingBook.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRemainingBook()).asObject());
        bookAvailability.setCellValueFactory(cellData -> {
            int remaining = cellData.getValue().getRemainingBook();
            String availability = (remaining == 0) ? "Not Available" : "Available";
            return new SimpleStringProperty(availability);
        });
        loadBooks();

        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    openBookDetails(selectedBook);
                }
            }
        });
    }

    /**
     * Tải danh sách sách vào TableView.
     */
    public void loadBooks() {
        ObservableList<Book> booksList = FXCollections.observableArrayList();

        try {
            Admin admin = new Admin(currentUser.getUsername(), currentUser.getRole());
            List<Book> bookList = admin.viewAllBooks();
            if (bookList != null) {
                booksList.addAll(bookList);
            } else {
                showInfoDialog("Không tìm thấy sách nào trong cơ sở dữ liệu.");
            }
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải sách: " + e.getMessage());
        }
        bookTable.setItems(booksList);
    }

    /**
     * Mở giao diện thêm sách khi nhấn nút "Thêm sách".
     */
    @FXML
    private void addBook() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/timsach.fxml"));
            Parent root = fxmlLoader.load();

            AddBookController controller = fxmlLoader.getController();
            controller.setHomeController(this);
            borderPane_book.setCenter(root);
        } catch (IOException e) {
            showErrorDialog("Lỗi khi mở giao diện thêm sách: " + e.getMessage());
        }
    }

    /**
     * Mở giao diện chi tiết sách khi nhấn đúp vào một cuốn sách trong bảng.
     * @param book Sách được chọn để xem chi tiết.
     */
    private void openBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            BookDetailController controller = fxmlLoader.getController();
            controller.setMenuController(menuController);
            controller.setBook(book);

            borderPane_book.setCenter(bookDetailsRoot);
        } catch (IOException e) {
            showErrorDialog("Lỗi khi hiển thị chi tiết sách: " + e.getMessage());
        }
    }

    /**
     * Hiển thị hộp thoại lỗi với thông báo được cung cấp.
     * @param message Thông báo lỗi cần hiển thị.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thông tin với thông báo được cung cấp.
     * @param message Thông báo thông tin cần hiển thị.
     */
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
