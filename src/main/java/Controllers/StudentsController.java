package Controllers;

import User.Admin;
import User.User;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller quản lý sinh viên trong ứng dụng.
 * Lớp này chịu trách nhiệm hiển thị danh sách sinh viên,
 * cho phép thêm, sửa, xóa và hiển thị chi tiết thông tin sinh viên.
 */
public class StudentsController { // Thay đổi tên lớp để tuân thủ quy tắc đặt tên

    @FXML
    private TableView<User> studentTable; // Khai báo TableView chứa danh sách sinh viên
    @FXML
    private TableColumn<User, Integer> studentID; // Cột ID sinh viên
    @FXML
    private TableColumn<User, String> studentName; // Cột tên sinh viên
    @FXML
    private TableColumn<User, String> studentNumber; // Cột số điện thoại sinh viên
    @FXML
    private TableColumn<User, String> studentEmail; // Cột email sinh viên
    @FXML
    private ImageView imageView;
    @FXML
    private TextField searchIDstudent; // TextField tìm kiếm sinh viên theo ID
    @FXML
    private TextField studentOfemail; // TextField hiển thị email của sinh viên
    @FXML
    private TextField studentOfid; // TextField hiển thị ID của sinh viên
    @FXML
    private TextField studentOfname; // TextField hiển thị tên của sinh viên
    @FXML
    private TextField studentOfnumber; // TextField hiển thị số điện thoại của sinh viên
    @FXML
    private TextField usernameStudent; // TextField hiển thị tên đăng nhập của sinh viên
    @FXML
    private TextField passwordStudent; // TextField hiển thị mật khẩu của sinh viên

    /**
     * Phương thức khởi tạo, được gọi khi Controller được khởi tạo.
     * Thiết lập các sự kiện và cấu hình ban đầu cho bảng và các TextField.
     */
    @FXML
    private void initialize() {
        // Kiểm tra nếu studentEmail không null
        studentID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        studentName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        studentNumber.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
        studentEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        // Đổi tên phương thức để khớp với cách gọi
        studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue != null) {
                showStudentDetails(newValue); // Hiển thị chi tiết sinh viên khi chọn
            }
        });
        loadStudents(); // Tải danh sách sinh viên
    }

    /**
     * Phương thức tải danh sách sinh viên từ cơ sở dữ liệu và hiển thị trong TableView.
     */
    private void loadStudents() { // Đổi tên phương thức cho thống nhất
        ObservableList<User> studentsList = FXCollections.observableArrayList();

        try {
            Admin admin = new Admin(currentUser.getUsername(),currentUser.getPassword());
            List<User> studentList = admin.viewAllMembers(); // Lấy danh sách sinh viên
            if (studentList != null) {
                studentsList.addAll(studentList); // Thêm các sinh viên vào danh sách
            } else {
                showAlbertDialog("Không có sinh viên trong cơ sở dữ liệu");
            }
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải sinh viên: " + e.getMessage()); // Hiển thị lỗi nếu có
        }
        studentTable.setItems(studentsList); // Đặt dữ liệu cho TableView
    }

    /**
     * Phương thức hiển thị thông tin chi tiết của sinh viên trong các TextField.
     * @param student đối tượng sinh viên cần hiển thị thông tin
     */
    private void showStudentDetails(User student) {
        studentOfid.setText(String.valueOf(student.getId())); // Hiển thị ID
        studentOfname.setText(student.getFullName()); // Hiển thị tên
        studentOfnumber.setText(student.getNumber()); // Hiển thị số điện thoại
        studentOfemail.setText(student.getEmail()); // Hiển thị email
        usernameStudent.setText(student.getUserName()); // Hiển thị tên đăng nhập
        passwordStudent.setText(student.getPassword()); // Hiển thị mật khẩu
    }

    /**
     * Phương thức xử lý sự kiện khi nhấn nút xóa sinh viên.
     * Xác nhận việc xóa và sau đó xóa sinh viên khỏi cơ sở dữ liệu và TableView.
     */
    @FXML
    private void DeleteAction() {
        User selectedStudent = studentTable.getSelectionModel().getSelectedItem(); // Lấy sinh viên được chọn
        if (selectedStudent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Hiển thị hộp thoại xác nhận xóa
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc muốn xoá sinh viên này không");

            if (alert.showAndWait().get() == ButtonType.OK) { // Nếu xác nhận xóa
                try {
                    Admin admin = new Admin(currentUser.getUsername(), currentUser.getPassword());
                    admin.removeUser(selectedStudent); // Xóa sinh viên khỏi cơ sở dữ liệu
                    studentTable.getItems().remove(selectedStudent); // Xóa sinh viên khỏi TableView
                    showAlbertDialog("Xoá thành công");
                } catch (SQLException e) {
                    throw new RuntimeException(e); // Xử lý lỗi SQL
                }
            }
        }
    }

    /**
     * Phương thức lưu thông tin sinh viên khi nhấn nút "Lưu".
     * Cập nhật thông tin sinh viên trong cơ sở dữ liệu và TableView.
     */
    @FXML
    private void saveAction() {
        User selectedStudent = studentTable.getSelectionModel().getSelectedItem(); // Lấy sinh viên được chọn
        if (selectedStudent != null) {
            // Lấy thông tin từ các TextField
            String fullName = studentOfname.getText();
            String number = studentOfnumber.getText();
            String email = studentOfemail.getText();
            String username = usernameStudent.getText();
            String password = passwordStudent.getText();
            int newId = Integer.parseInt(studentOfid.getText());  // Lấy ID mới từ TextField

            // Cập nhật thông tin người dùng
            selectedStudent.setFullName(fullName);
            selectedStudent.setNumber(number);
            selectedStudent.setEmail(email);
            // selectedStudent.setUserName(username);  // Cập nhật tên đăng nhập nếu cần
            selectedStudent.setPassword(password);
            selectedStudent.setId(newId);  // Cập nhật ID mới
            try {
                Admin admin = new Admin(currentUser.getUsername(), currentUser.getPassword());
                boolean success = admin.updateUser(selectedStudent);  // Cập nhật thông tin người dùng
                if (success) {
                    // Cập nhật lại dữ liệu trong TableView
                    studentTable.refresh();
                    showAlbertDialog("Cập nhật thành công");
                } else {
                    showAlbertDialog("Cập nhật thất bại");
                }
            } catch (SQLException e) {
                showErrorDialog("Lỗi khi cập nhật thông tin : " + e.getMessage());
            }
        }
    }

    /**
     * Hiển thị hộp thoại lỗi cho người dùng.
     * @param message thông điệp lỗi cần hiển thị
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thông báo cho người dùng.
     * @param message thông điệp thông báo cần hiển thị
     */
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
