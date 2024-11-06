package controllers;

import Document.Book; // Import lớp Book
import User.Admin;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
                System.out.println("không có sách trong cơ sở dữ liệu");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải sách: " + e.getMessage());
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
            e.printStackTrace();
        }
    }

    private void openBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();
           bookDetailCotroller controller = fxmlLoader.getController();
           controller.setMenuController(menuController);
           controller.setBook(book);
            //bookDetailCotroller controller = fxmlLoader.getController();
            //controller.setBook(book);
            //BorderPane borderPane = menuController.getBorderPane();
            //borderPane.setCenter(bookDetailsRoot);
            borderPane_book.setCenter(bookDetailsRoot);
        } catch (IOException e) {

        }

    }
}
