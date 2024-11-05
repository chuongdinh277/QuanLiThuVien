package controllers;

import Document.Book; // Import lớp Book
import User.Admin;
import User.User;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class students { // Thay đổi tên lớp để tuân thủ quy tắc đặt tên

    @FXML
    private TableView<User> studentTable; // Khai báo TableView
    @FXML
    private TableColumn<User, Integer> studentID; // Cột ID
    @FXML
    private TableColumn<User, String> studentName; // Cột tên
    @FXML
    private TableColumn<User, String> studentNumber; // Cột số điện thoại
    @FXML
    private TableColumn<User, String> studentEmail; // Cột email

    @FXML
    private TextField studentOfemail; // Sửa tên biến theo quy tắc CamelCase
    @FXML
    private TextField studentOfid;
    @FXML
    private TextField studentOfname;
    @FXML
    private TextField studentOfnumber;

    @FXML
    private void initialize() {
        // Kiểm tra nếu studentEmail không null
            studentID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            studentName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
            studentNumber.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
            studentEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
             // Đổi tên phương thức để khớp với cách gọi
            studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
                if (newValue != null) {
                    showStudentDetails(newValue);
                }
            });
        loadStudents();
    }

    private void loadStudents() { // Đổi tên phương thức cho thống nhất
        ObservableList<User> studentsList = FXCollections.observableArrayList();

        try {
            Admin admin = new Admin(currentUser.getUsername(),currentUser.getPassword());
            List<User> studentList = admin.viewAllMembers();
            if (studentList != null) {
                studentsList.addAll(studentList);
            } else {
                System.out.println("Không có sinh viên trong cơ sở dữ liệu");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải sinh viên: " + e.getMessage());
        }
        studentTable.setItems(studentsList); // Đặt dữ liệu cho TableView
    }
    private void showStudentDetails(User student) {
        studentOfid.setText(String.valueOf(student.getId()));
        studentOfname.setText(student.getFullName());
        studentOfnumber.setText(student.getNumber());
        studentOfemail.setText(student.getEmail());
    }
}
