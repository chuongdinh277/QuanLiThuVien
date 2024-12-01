package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import User.User;
public class updateStudentController
{

    @FXML
    private TextField MSSVupdate;

    @FXML
    private TextField NameofStudent;

    @FXML
    private TextField NumberOfstudent;

    @FXML
    private Button UpdateButton;

    @FXML
    private TextField emailOfstudent;

    @FXML
    private TextField usernameStudent;

    @FXML
    private TextField PasswordStudent;

    public void setUserInfo(int mssv, String name, String number, String email, String username, String password) {
        MSSVupdate.setText(String.valueOf(mssv));
        NameofStudent.setText(name);
        NumberOfstudent.setText(number);
        emailOfstudent.setText(email);
        usernameStudent.setText(username);
        PasswordStudent.setText(password);
    }

    @FXML
    public void updateAction(ActionEvent event) {
        int mssv = Integer.parseInt( MSSVupdate.getText());
        String name = NameofStudent.getText();
        String number = NumberOfstudent.getText();
        String email = emailOfstudent.getText();
        String username = usernameStudent.getText();
        String password = PasswordStudent.getText();

        // Kiểm tra các trường có bị bỏ trống không
        if (mssv==0 || name.isEmpty() || number.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Cập nhật thông tin vào cơ sở dữ liệu
        User updatedUser = new User(mssv, username, password, "user", name, email, number);
       // updatedUser.updateStudentMSVN(username,mssv); // Giả sử bạn có phương thức setMssv() để cập nhật mã sinh viên

        try {
            boolean updateSuccess = updatedUser.updateStudentMSVN(username,mssv); // Cập nhật vào cơ sở dữ liệu
            if (updateSuccess) {
                showAlert("Thông báo", "Cập nhật thành công!");
                // Sau khi cập nhật thành công, quay lại trang đăng nhập
                //closeWindow(event);
            } else {
                showAlert("Lỗi", "Cập nhật không thành công. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            showError("Có lỗi xảy ra " + e.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
