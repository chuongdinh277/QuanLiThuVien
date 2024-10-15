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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class homeController {

    @FXML
    private TableView<Book> bookTable; // Khai báo TableView
    @FXML
    private TableColumn<Book, Integer> bookID; // Cột ID
    @FXML
    private TableColumn<Book, String> nameOfbook; // Cột tên sách
    @FXML
    private TableColumn<Book, String> authorOfbook; // Cột tác giả
    @FXML
    private TableColumn<Book, Button> actionOfbook; // Cột hành động

    @FXML
    private void initialize() {

        bookID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameOfbook.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorOfbook.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        actionOfbook.setCellValueFactory(cellData -> {
            Button button = new Button("Xem");
            button.setOnAction(event -> {
                // Xử lý hành động khi nhấn nút
                System.out.println("Đã nhấn nút xem cho sách: " + cellData.getValue().getTitle());
            });
            return new SimpleObjectProperty<>(button);
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
}
