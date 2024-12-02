package Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import User.currentUser;
import User.User;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class profileController {

    @FXML
    private ImageView editPassword;

    @FXML
    private ImageView editPhoneNumber;

    @FXML
    private MenuItem editProfileButton;

    @FXML
    private ImageView editfullName;

    @FXML
    private MenuButton logoutAndEditProfile;

    @FXML
    private MenuItem logoutButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField repasswordTextField;

    @FXML
    private Label role;

    @FXML
    private ImageView starbook1;

    @FXML
    private ImageView starbook2;

    @FXML
    private ImageView starbook3;

    @FXML
    private ImageView starbook4;

    @FXML
    private ImageView starbook5;

    @FXML
    private Label username;

    @FXML
    private TextField fullnameTextfield;
    @FXML
    private Label fullnameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Label emailLabel1;
    @FXML
    private Label phoneLabel1;
    @FXML
    private AnchorPane anchorPane;

    private boolean isEditingFullName = false; // Cờ kiểm tra nếu đang sửa Tên
    private boolean isEditingPhone = false;   // Cờ kiểm tra nếu đang sửa Số điện thoại
    private boolean isEditingPassword = false; // Cờ kiểm tra nếu đang sửa Mật khẩu

    User newUser;
    public void initialize() {
        try {
            newUser = User.loadUserDetailsByUsername(currentUser.getUsername());
            username.setText(newUser.getUserName());
            role.setText(newUser.getRole());
            fullnameLabel.setText(newUser.getFullName());
            phoneLabel.setText(newUser.getNumber());
            phoneLabel1.setText(newUser.getNumber());
            emailLabel.setText(newUser.getEmail());
            emailLabel1.setText(newUser.getEmail());
            usernameLabel.setText(newUser.getUserName());
            passwordLabel.setText(newUser.getPassword());
            roleLabel.setText(newUser.getRole());

            fullnameTextfield.setVisible(false);
            phoneTextField.setVisible(false);
            passwordTextField.setVisible(false);
        } catch (SQLException e) {

        }
    }
    @FXML
    private void handlePersonClick(ActionEvent event) {

    }

    @FXML
    private void handleStarclick(MouseEvent event) {

    }

    @FXML
    private void logout_Button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại từ bất kỳ Node nào
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void onEditFullNameClick() {
        isEditingFullName = true;
        fullnameLabel.setVisible(false);
        fullnameTextfield.setVisible(true);
        // fullnameTextfield.setText(fullnameLabel.getText());
    }

    @FXML
    private void onEditPhoneNumberClick() {
        isEditingPhone = true;
        phoneLabel.setVisible(false);
        phoneTextField.setVisible(true);
        //phoneTextField.setText(phoneLabel.getText());
    }

    @FXML
    private void onEditPasswordClick() {
        isEditingPassword = true;
        passwordLabel.setVisible(false);
        passwordTextField.setVisible(true);
        passwordTextField.setText(""); // Để trống để nhập mật khẩu mới
    }
    @FXML
    private void onSaveButtonClick() {


        if (isEditingFullName) {
            String newFullName = fullnameTextfield.getText();
            newUser.setFullName(newFullName);
            fullnameLabel.setText(newFullName);
            fullnameLabel.setVisible(true);
            fullnameTextfield.setVisible(false);
            isEditingFullName = false;
        }

        if (isEditingPhone) {
            String newPhoneNumber = phoneTextField.getText();
            newUser.setNumber(newPhoneNumber);
            phoneLabel.setText(newPhoneNumber);
            phoneLabel.setVisible(true);
            phoneTextField.setVisible(false);
            isEditingPhone = false;
        }

        if (isEditingPassword) {
            String newPassword = passwordTextField.getText();
            if (!newPassword.isEmpty()) { // Chỉ cập nhật nếu nhập mật khẩu mới
                newUser.setPassword(newPassword);
            }
            passwordLabel.setText("********"); // Hiển thị mật khẩu ẩn
            passwordLabel.setVisible(true);
            passwordTextField.setVisible(false);
            isEditingPassword = false;
        }
        currentUser.setUsername(newUser.getUserName());
        currentUser.setFullName(newUser.getFullName());
        currentUser.setPassword(newUser.getPassword());
        currentUser.setEmail(newUser.getEmail());
        currentUser.setNumber(newUser.getNumber());
        if(newUser == null) System.out.println("null");
        System.out.println(newUser.getFullName());
        // (Tùy chọn) Ghi thông tin vào database
        User.updateDatabase(newUser.getFullName(), newUser.getNumber(), newUser.getPassword(), newUser.getUserName());
    }
}