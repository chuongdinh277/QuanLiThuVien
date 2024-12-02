package Controllers;

import User.currentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import static APIGoogle.GoogleBooksAPI.showErrorDialog;

/**
 * Controller quản lý menu dành cho Admin.
 */
public class MenuAdminController {
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

    /**
     * Phương thức khởi tạo controller, hiển thị thông tin người dùng hiện tại
     * và tải giao diện bảng điều khiển.
     */
    public void initialize() {
        username.setText(currentUser.getUsername());
        role.setText(currentUser.getRole());
        loadDashboard();
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn vào nút xem thông tin cá nhân.
     * Tải giao diện profile của người dùng vào trung tâm của BorderPane.
     */
    @FXML
    private void handlePersonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/profile.fxml"));
            AnchorPane personRoot = loader.load();
            borderPane_admin.setCenter(personRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Lỗi khi tải giao diện person.fxml: " + e.getMessage());
        }
    }

    /**
     * Đăng xuất người dùng hiện tại và chuyển về giao diện đăng nhập.
     */
    @FXML
    private void logout_Button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) borderPane_admin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải giao diện hiển thị danh sách sách vào trung tâm của BorderPane.
     */
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

    /**
     * Tải giao diện danh sách sinh viên vào trung tâm của BorderPane.
     */
    @FXML
    private void userButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/students.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị giao diện danh sách sách vào trung tâm BorderPane.
     */
    public void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/allBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải giao diện cho chức năng mượn sách.
     */
    @FXML
    private void loadIssueBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/issueBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải giao diện danh sách sách đã mượn vào trung tâm BorderPane.
     */
    @FXML
    private void loadAllIssueBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/allIssueBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải giao diện trả sách vào trung tâm BorderPane.
     */
    @FXML
    private void loadReturnBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/returnBook.fxml"));
            Parent root = loader.load();
            borderPane_admin.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải giao diện bảng điều khiển (dashboard) vào trung tâm BorderPane.
     */
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
        }
    }
}
