package controllers;
import User.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static Database.DatabaseConnection.getConnection;

public class UserProfileController {

    @FXML
    private AnchorPane informationPane;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label fullNameLabel1;
    @FXML
    private Label idLabel;
    @FXML
    private Label idLabel1;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField passwordTextField;

    @FXML
    private Button editButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    @FXML
    private void initialize() {
        System.out.println("Full name : " + currentUser.getFullName());
        emailTextField.setText(currentUser.getEmail());
        usernameLabel.setText(currentUser.getUsername());
        passwordTextField.setText(currentUser.getPassword());
        fullNameLabel.setText(currentUser.getFullName());
        fullNameLabel1.setText(currentUser.getFullName());
        idLabel.setText(String.valueOf(currentUser.getId()));
        idLabel1.setText(String.valueOf(currentUser.getId()));
        emailTextField.setEditable(false);
        passwordTextField.setEditable(false);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

    }

    @FXML
    private void handleEditButton() {
        System.out.println("clicked edit button");
        updateButton.setVisible(true);
        cancelButton.setVisible(true);
        // Enable editing fields
        emailTextField.setEditable(true);
        passwordTextField.setEditable(true);
    }

    @FXML
    private void handleUpdateButton() {
        System.out.println("clicked update button");
        String newEmail = emailTextField.getText();
        String newPassword = passwordTextField.getText();

        // Cập nhật dữ liệu người dùng
        currentUser.setEmail(newEmail);
        currentUser.setPassword(newPassword);

        // Disable editing fields
        emailTextField.setEditable(false);
        passwordTextField.setEditable(false);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        String sql = "UPDATE users SET email = ?, password = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUser.getEmail());
            pstmt.setString(2, currentUser.getPassword());
            pstmt.setInt(3, currentUser.getId());

            pstmt.executeUpdate();
            System.out.println("User updated successfully.");

            // Hiển thị thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cập nhật thành công");
            alert.setHeaderText(null);
            alert.setContentText("Thông tin người dùng đã được cập nhật thành công.");
            alert.showAndWait();

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());

            // Hiển thị thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi cập nhật");
            alert.setHeaderText("Không thể cập nhật thông tin");
            alert.setContentText("Đã xảy ra lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelButton() {
        System.out.println("clicked cancel button");
        // Reset editing fields
        emailTextField.setText(currentUser.getEmail());
        passwordTextField.setText(currentUser.getPassword());
        // Disable editing fields
        emailTextField.setEditable(false);
        passwordTextField.setEditable(false);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);
    }
}
