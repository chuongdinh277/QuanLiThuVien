package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import User.Admin;
import User.User;
import User.currentUser;
import User.Member;
public class loginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordHiddenlogin;
    @FXML
    private TextField usernameFieldlogin;

    @FXML
    private PasswordField PasswordHiddenRL;
    @FXML
    private PasswordField passwordHidden;

    @FXML
    private Button signUpButton;

    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private ChoiceBox<String> myChoiceBox;

    @FXML
    private Button loginReturn;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField emailField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myChoiceBox.getItems().add("admin");
        myChoiceBox.getItems().add("user");
    }

    // Xử lí đăng kí tài khoản
    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordHidden.getText();
        String passwordConfirm = PasswordHiddenRL.getText();
        String number = numberField.getText();
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String role = myChoiceBox.getValue();
        if(username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || role.isEmpty() || number.isEmpty()
         || email.isEmpty() || fullName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("lỗi đăng kí");
                alert.setHeaderText("Vui lòng điền đầy đủ thông tin");
                alert.showAndWait();
                return;
        }

        if(!passwordConfirm.equals(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("lỗi đăng kí");
            alert.setHeaderText("Mật khẩu nhập lại không đúng");
            alert.showAndWait();
            return;
        }

        User newUser = new User(username, password, role, fullName, email, number);

        try {
            if (newUser.isUsernameTaken(username)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Tên tài khoản đã tồn tại");
                alert.setContentText("Vui lòng chọn tên tài khoản khác");
                alert.showAndWait();
            } else {
                // Tiến hành đăng ký
                newUser.register();
                // Chuyển đến trang khác nếu đăng ký thành công
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Hiển thị thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Đăng ký thất bại");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    //xử lí đăng nhập tài khoản

    public void handleLogin(ActionEvent event) {
        String username = usernameFieldlogin.getText();
        String password = passwordHiddenlogin.getText();

        try {
            // Gọi phương thức login từ lớp User
            String role = User.getRoleUser(username, password); // Gọi phương thức login và nhận lại vai trò
            String rolecheck = myChoiceBox.getValue();
            // Kiểm tra vai trò
            if(rolecheck.equals(role)) {
                if ("admin".equals(role)) {
                    currentUser.setUsername(username);
                    currentUser.setRole(role);
                    // Chuyển đến trang Admin
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1500, 900));
                    stage.show();
                } else if ("user".equals(role)) {
                    currentUser.setUsername(username);
                    currentUser.setRole(role);
                    // Chuyển đến trang Member
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1500, 700));
                    stage.show();
                } else {
                    // Thông báo lỗi nếu không phải Admin hoặc Member
                    showAlert("Error", "Đăng nhập thất bại: " + role);
                }
            }
            else {
                showAlert("Error", "Đăng nhập thất bại: " + role);
            }
        } catch (SQLException e) {
            showAlert("Error", "Đăng nhập thất bại: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void signUp(ActionEvent event) {
        // Logic cho việc chuyển sang trang đăng ký
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loginReturn(ActionEvent event) {
        // Logic cho việc chuyển sang trang đăng nhập
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object getclass() {
        return getClass();
    }
}