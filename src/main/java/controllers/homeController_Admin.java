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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
public class homeController_Admin {

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
    private Button addBook_Admin;

    @FXML
    private void initialize() {

        bookID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        bookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bookAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        bookPublisher.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPublisher()));
        bookQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        bookSection.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSection()));

        bookAction.setCellValueFactory(cellData -> {
            HBox hBox = new HBox();
            Button buttoncheck = new Button("Xem");
            Button buttonupdate = new Button("update");
            buttoncheck.setOnAction(event -> {
                // Xử lý hành động khi nhấn nút
                //System.out.println("Đã nhấn nút xem cho sách: " + cellData.getValue().getTitle());
            });
            buttonupdate.setOnAction(event -> {

            });
            hBox.getChildren().addAll(buttoncheck, buttonupdate);
            return new SimpleObjectProperty<>(hBox);
           // return new SimpleObjectProperty<>(buttonupdate);
        });

        loadBooks();
    }
    private void loadBooks() {
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

            Stage stage = new Stage();
            stage.setTitle("Add Book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Để cửa sổ này là modal (khóa cửa sổ chính)
            stage.showAndWait(); // Đợi cho đến khi cửa sổ được đóng
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
