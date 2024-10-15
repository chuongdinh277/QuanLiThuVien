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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myChoiceBox.getItems().add("Admin");
        myChoiceBox.getItems().add("User");
    }

    // Xử lí đăng kí tài khoản
    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordHidden.getText();
        String passwordConfirm = PasswordHiddenRL.getText();
        String role = myChoiceBox.getValue();
        if(username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || role.isEmpty()) {
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

        User newUser = new User(username, password, role);

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
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
        String role = myChoiceBox.getValue();

        // Kiểm tra thông tin người dùng
        User user = new User(username, password, role);
        try {
            // Kiểm tra tên đăng nhập và mật khẩu
            if (!user.isUsernameTaken(username)) {
                showAlert("Error", "Tài khoản không tồn tại. Vui lòng kiểm tra lại tên đăng nhập.");
                return;
            }

            user.login();
            // Lưu thông tin người dùng vào CurrentUser (nếu bạn đã tạo lớp này)
            currentUser.setUsername(username);
            currentUser.setRole(role); // Nếu có lớp lưu trữ vai trò

            // Chuyển đến trang chính

            if(role.equals("Admin")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1300, 700);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            else if(role.equals("User")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1300, 700);
                // Hiển thị scene mới
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
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
