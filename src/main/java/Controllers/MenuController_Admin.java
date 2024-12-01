package Controllers;

import User.currentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static APIGoogle.GoogleBooksAPI.showErrorDialog;

public class MenuController_Admin {
    @FXML
    private BorderPane borderPane_admin;
    private Parent homeView;
    @FXML
    private Button issueBook;
    @FXML
    private Label username;
    @FXML
    private Label role;
    @FXML
    private MenuButton logoutAndEditProfile;
    @FXML
    private MenuItem editProfileButton;
    @FXML
    private MenuItem logoutButton;
    public void initialize() {
        username.setText(currentUser.getUsername());
        role.setText(currentUser.getRole());
        loadDashboard();
    }
    @FXML
    private void handlePersonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/profile.fxml"));
            AnchorPane personRoot = loader.load();
            borderPane_admin.setCenter(personRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("L��i khi tải giao diện person.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void logout_Button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại từ bất kỳ Node nào
            Stage stage = (Stage) borderPane_admin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void loadHomeview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/allBook.fxml"));
            homeView = loader.load();
            AllBookController homeController = loader.getController();
            homeController.setMenuController(this);
            borderPane_admin.setCenter(homeView);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    @FXML
    private void bookStoreLoad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/timsach.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
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
            System.out.println("Error loading allBook.fxml: " + e.getMessage());
        }
    }

    public void showHome() {
        try {
            // Tải giao diện chính (ví dụ allBook.fxml) vào BorderPane
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/allBook.fxml"));
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

            DashboardController dashboardController = loader.getController();
            dashboardController.setMainBorderPane(borderPane_admin);
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading home page: " + e.getMessage());
        }
    }
}
