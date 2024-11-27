package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MenuController_Admin {
    @FXML
    private Label name;
    @FXML
    private Label role;
    @FXML
    private HBox loveBooksHBox;
    @FXML
    private VBox menuVBox;
    @FXML
    private Button menuButton;
    @FXML
    private Label homeLabel, systemLabel;
    @FXML
    private BorderPane borderPane_admin;
    private Parent homeView;
    @FXML
    private ImageView logo;
    public BorderPane getBorderPane() {
        return borderPane_admin;
    }
    @FXML
    private Button issueBook;
    public void initialize() {
        // Hiển thị tên người dùng và vai trò
       // name.setText(currentUser.getUsername());
       // role.setText(currentUser.getRole());
        loadDashboard();

    }

    @FXML
    private void loadHomeview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home.fxml"));
            homeView = loader.load();
          //  Parent root = loader.load();
            homeController_Admin homeController = loader.getController();
            homeController.setMenuController(this);
            borderPane_admin.setCenter(homeView);
        } catch (IOException e) {
            e.printStackTrace(); // In ra lỗi chi tiết
            System.out.println("Error loading home.fxml: " + e.getMessage());
        }
    }
    @FXML
    private void bookStoreLoad() {
        try {
            // Tải file FXML vào root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/timsach.fxml"));
            Parent root = loader.load();

            // Đặt root vào center của BorderPane hiện tại
            borderPane_admin.setCenter(root);

            // Bạn có thể in ra thông báo để xác nhận việc tải thành công
            System.out.println("Loaded timsach.fxml into center of BorderPane.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading timsach.fxml: " + e.getMessage());
        }
    }
    @FXML
    private void userButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/students.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace(); // In ra lỗi chi tiết
            System.out.println("Error loading home.fxml: " + e.getMessage());
        }
    }

    public void showHome() {
        try {
            // Tải giao diện chính (ví dụ home.fxml) vào BorderPane
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading home page: " + e.getMessage());
        }
    }

    @FXML
    private void loadIssueBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/issueBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading home page: " + e.getMessage());
        }
    }
    @FXML
    private void loadAllIssueBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/allIssueBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading home page: " + e.getMessage());
        }
    }
    @FXML
    private void loadReturnBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/returnBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading home page: " + e.getMessage());
        }
    }

    @FXML
    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();

            dashboardController dashboardController = loader.getController();
            dashboardController.setMainBorderPane(borderPane_admin);
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading home page: " + e.getMessage());
        }
    }
}
