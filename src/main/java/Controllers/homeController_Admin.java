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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import javafx.scene.chart.BarChart;
//import static controllers.MenuController_Admin.borderPane_admin;


public class homeController_Admin {

    @FXML
    private BorderPane borderPane_book;
    @FXML
    private TableView<Book> bookTable; // Khai báo TableView
    @FXML
    private TableColumn<Book, Integer> bookID; // Cột ID
    @FXML
    private TableColumn<Book, String> bookTitle; // Cột tên sách
    @FXML
    private TableColumn<Book, String> bookAuthor; // cột tác giar
    @FXML
    private TableColumn<Book, String> bookPublisher; // cột nhà xuất bản
    @FXML
    private TableColumn<Book, Integer> bookQuantity; // cột số lượng
    @FXML
    private TableColumn<Book, HBox> bookAction; //cột hành động
    @FXML
    private TableColumn<Book, String> bookSection; // cột chủ đề;
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
    private static MenuController_Admin menuController;
    public void setMenuController(MenuController_Admin menuController) {
        this.menuController = menuController;
    }
    @FXML
    private void initialize() {

        //bookID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        ISBNbook.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getISBN()));
        bookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bookAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        bookPublisher.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPublisher()));
        bookQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        remainingBook.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRemainingBook()).asObject());
        bookSection.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSection()));
        bookAvailability.setCellValueFactory(cellData -> {
            int remaining = cellData.getValue().getRemainingBook();
            String availability = (remaining == 0) ? "Not available" : "Available";
            return new SimpleStringProperty(availability);
        });
        loadBooks();

        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Book selectBook = bookTable.getSelectionModel().getSelectedItem();
                if(selectBook != null) {
                    openBookDetails(selectBook);
                }
            }
        });

    }
    public void loadBooks() {
        ObservableList<Book> booksList = FXCollections.observableArrayList();

        try {
            Admin admin = new Admin(currentUser.getUsername(), currentUser.getRole());
            List<Book> bookList = admin.viewAllBooks();
            if(bookList != null) {
                booksList.addAll(bookList);
            }
            else {
                showAlbertDialog("không có sách trong cơ sở dữ liệu");
            }
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải sách: " + e.getMessage());
        }
        bookTable.setItems(booksList); // Đặt dữ liệu cho TableView
    }

    @FXML
    private void addBook(){
        // Xử lý hành đ��ng khi nhấn nút thêm sách
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/timsach.fxml"));
            Parent root = fxmlLoader.load();

            addBook controller = fxmlLoader.getController();
            controller.setHomeController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Để cửa sổ này là modal (khóa cửa sổ chính)
            stage.showAndWait(); // Đợi cho đến khi cửa sổ được đóng
        } catch (IOException e) {
            showErrorDialog("Lỗi khi thêm sách " + e.getMessage());
        }
    }

    private void openBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            bookDetailCotroller controller = fxmlLoader.getController(); // Chú ý chữ hoa
            controller.setMenuController(menuController); // Đảm bảo phương thức này tồn tại
            controller.setBook(book); // Đảm bảo phương thức này tồn tại

            borderPane_book.setCenter(bookDetailsRoot); // Kiểm tra null trước khi sử dụng
        } catch (IOException e) {
            e.printStackTrace(); // In ra lỗi chi tiết để kiểm tra
            showErrorDialog("Lỗi khi hiển thị sách: " + e.getMessage());
        }
    }


    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
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
