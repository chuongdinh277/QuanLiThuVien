package controllers;

import User.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class userViewController {
    @FXML
    private BorderPane borderPane_User;
    @FXML
    private Button bookLibrary;
    @FXML
    private Button bookBorrowed;
    @FXML
    private Button personInformation;
    @FXML
    private Button logout;
    @FXML
    private Button returnBook;

    @FXML
    private TextField searchBook;
    @FXML
    private Button searchButton;
    @FXML
    private HBox library;
    @FXML
    private HBox borrowed;
    @FXML
    private HBox person;
    @FXML
    private HBox exit;
    @FXML
    private Label username;
    @FXML
    private Label role;
    @FXML
    private ImageView avatar;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private ImageView libraryIcon;

    @FXML
    private ImageView borrowedIcon;
    @FXML
    private ImageView personIcon;
    @FXML
    private void initialize() {
        // Hiển thị tên người dùng và vai trò
       username.setText(currentUser.getUsername());
       role.setText(currentUser.getRole());
        addHoverEffect(library);
        addHoverEffect(borrowed);
        addHoverEffect(person);
        addHoverEffect(exit);
        addHoverEffect(searchBook);
        addHoverEffect(searchButton);
        addHoverEffect(avatar);
        setButtonTextColor(bookLibrary, defaultColor);
        setButtonTextColor(bookBorrowed, defaultColor);
        setButtonTextColor(personInformation, defaultColor);
//        avatar.setOnMouseEntered(event -> showInfo());
//        avatar.setOnMouseExited(event -> hideInfo());
    }

    private final Color defaultColor = Color.valueOf("#99a5b7");
    private final Color activeColor = Color.valueOf("#4969D9");

    @FXML
    void handleLibraryClick() {
        resetButtonStyles(); // Đặt lại màu cho tất cả các nút
        changeButtonStyle(bookLibrary, libraryIcon);
    }

    @FXML
    void handleBorrowedClick() {
        resetButtonStyles(); // Đặt lại màu cho tất cả các nút
        changeButtonStyle(bookBorrowed, borrowedIcon);
    }

    @FXML
    void handlePersonClick() {
        resetButtonStyles(); // Đặt lại màu cho tất cả các nút
        changeButtonStyle(personInformation, personIcon);
    }

    private void changeButtonStyle(Button button, ImageView imageView) {
        button.setTextFill(activeColor); // Đổi màu chữ thành đen
        imageView.setStyle("-fx-background-color: #4969D9;"); // Đổi màu nền
    }

    private void resetButtonStyles() {
        setButtonTextColor(bookLibrary, defaultColor);
        setButtonTextColor(bookBorrowed, defaultColor);
        setButtonTextColor(personInformation, defaultColor);

        // Đặt lại màu nền cho các ImageView
        libraryIcon.setStyle("-fx-background-color: transparent;");
        borrowedIcon.setStyle("-fx-background-color: transparent;");
        personIcon.setStyle("-fx-background-color: transparent;");
    }

    private void setButtonTextColor(Button button, Color color) {
        button.setTextFill(color);
    }

    private void addHoverEffect(Node node) {
        if (node != null) {
            node.setOnMouseEntered(event -> {
                node.setScaleX(1.1);
                node.setScaleY(1.1);
            });

            node.setOnMouseExited(event -> {
                node.setScaleX(1.0);
                node.setScaleY(1.0);
            });
        }

    }

    @FXML
    private void bookLibrary_Button() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/viewBookInLibrary.fxml"));
            Parent root = loader.load();
            borderPane_User.setCenter(root);
            showAlbertDialog("Tải thành công");
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải FXML: " + e.getMessage());
             // In chi tiết lỗi ra console
        }
    }

    @FXML
    private void bookBorrowed_Button ( ) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/viewBookBorrowed.fxml"));
            Parent root = loader.load();
            borderPane_User.setCenter(root);
            showAlbertDialog("Tải thành công");
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải FXML: " + e.getMessage());
             // In chi tiết lỗi ra console
        }

    }
    @FXML
    private void personInformation_Button (ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userProfile.fxml"));
            Parent root = loader.load();
            borderPane_User.setCenter(root);
            System.out.println("Tải thành công");
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải FXML: " + e.getMessage());
        }
    }

    @FXML
    private void logout_Button (ActionEvent event) {
        currentUser.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
        }
    }

    @FXML
    private void search_Button(ActionEvent event) {
        try {
            // Tải giao diện tìm kiếm sách
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/viewBooksSearchResult.fxml"));
            Parent root = loader.load();

            // Truyền truy vấn tìm kiếm vào ViewBooksSearchResult
            ViewBooksSearchResult controller = loader.getController();
            String searchQuery = searchBook.getText(); // Lấy truy vấn từ TextField
            controller.setSearchQuery(searchQuery); // Giả sử bạn thêm phương thức này trong controller

            // Cập nhật BorderPane với giao diện tìm kiếm
            borderPane_User.setCenter(root);
            showAlbertDialog("Tải thành công");
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải FXML: " + e.getMessage());
        }
    }

    private void showErrorDialog( String message) {
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
