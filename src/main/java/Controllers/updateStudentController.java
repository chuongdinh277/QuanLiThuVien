package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import User.User;

/**
 * Controller để xử lý cập nhật thông tin sinh viên.
 * Lớp này quản lý các hành động trong giao diện người dùng cập nhật thông tin sinh viên.
 */
public class updateStudentController {

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

    /**
     * Phương thức thiết lập thông tin sinh viên để hiển thị trong các trường văn bản.
     *
     * @param mssv Mã số sinh viên
     * @param name Tên sinh viên
     * @param number Số điện thoại sinh viên
     * @param email Email sinh viên
     * @param username Tên đăng nhập của sinh viên
     * @param password Mật khẩu của sinh viên
     */
    public void setUserInfo(int mssv, String name, String number, String email, String username, String password) {
        MSSVupdate.setText(String.valueOf(mssv));
        NameofStudent.setText(name);
        NumberOfstudent.setText(number);
        emailOfstudent.setText(email);
        usernameStudent.setText(username);
        PasswordStudent.setText(password);
    }

    /**
     * Phương thức được gọi khi người dùng nhấn nút "Cập nhật".
     * Phương thức này sẽ lấy thông tin từ các trường văn bản và cập nhật thông tin sinh viên vào cơ sở dữ liệu.
     *
     * @param event Sự kiện nhấn nút
     */
    @FXML
    public void updateAction(ActionEvent event) {
        int mssv = Integer.parseInt(MSSVupdate.getText());
        String name = NameofStudent.getText();
        String number = NumberOfstudent.getText();
        String email = emailOfstudent.getText();
        String username = usernameStudent.getText();
        String password = PasswordStudent.getText();

        // Kiểm tra các trường có bị bỏ trống không
        if (mssv == 0 || name.isEmpty() || number.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Cập nhật thông tin vào cơ sở dữ liệu
        User updatedUser = new User(mssv, username, password, "user", name, email, number);
        try {
            boolean updateSuccess = updatedUser.updateStudentMSVN(username, mssv); // Cập nhật vào cơ sở dữ liệu
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

    /**
     * Hiển thị hộp thoại thông báo với tiêu đề và thông điệp đã chỉ định.
     *
     * @param title Tiêu đề của thông báo
     * @param message Nội dung của thông báo
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thông báo lỗi.
     *
     * @param message Nội dung thông báo lỗi
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
