package Controllers;

import Document.Book;
import Document.BookDAO;
import Document.Transaction;
import Document.TransactionDAO;
import User.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Lớp điều khiển giao diện trả sách trong ứng dụng quản lý thư viện.
 * Lớp này xử lý các sự kiện và thao tác liên quan đến việc trả sách của sinh viên,
 * bao gồm việc tìm kiếm thông tin sinh viên, hiển thị thông tin giao dịch,
 * và thực hiện thao tác trả sách.
 */
public class ReturnBookController {

    @FXML
    private TextField IDsearchStudent;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, Integer> STTcolumn;

    @FXML
    private TableColumn<Transaction, String> ISBNColumn;

    @FXML
    private TableColumn<Transaction, String> bookAuthorColumn;

    @FXML
    private TableColumn<Transaction, String> bookTitleColumn;

    @FXML
    private BorderPane borderPane_book;

    @FXML
    private TableColumn<Transaction, Date> borrowDateColumn;

    @FXML
    private TextField nameStudentTextfield;

    @FXML
    private TableColumn<Transaction, Date> returnDateColumn;

    @FXML
    private TableColumn<Transaction, Void> actionColumn;

    @FXML
    private TableColumn<Transaction, Integer> quantityColumn;

    @FXML
    private TextField studentEmailTextfield;

    @FXML
    private TextField studentNumberTextfield;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    /**
     * Phương thức khởi tạo của lớp, cấu hình các cột trong bảng và gán sự kiện cho các ô tìm kiếm.
     */
    @FXML
    private void initialize() {
        IDsearchStudent.setOnAction(event -> loadStudentInfo());

        // Cấu hình các cột trong bảng
        STTcolumn.setCellValueFactory(cellData -> {
            int index = transactionList.indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        ISBNColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_Date"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("return_Date"));

        actionColumn.setCellFactory(col -> new TableCell<Transaction, Void>() {
            private final Button returnButton = new Button("Return Book");

            {
                // Thêm hành động cho nút trả sách
                returnButton.setOnAction(event -> handleReturnBook(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(returnButton);
                }
            }
        });
        // Liên kết danh sách với bảng
        transactionTable.setItems(transactionList);
    }

    /**
     * Xử lý sự kiện khi nhấn nút trả sách.
     * Cập nhật lại thông tin giao dịch và số lượng sách trong cơ sở dữ liệu.
     *
     * @param transaction Giao dịch cần trả sách
     */
    private void handleReturnBook(Transaction transaction) {
        // Xử lý sự kiện trả sách
        try {
            Book book = BookDAO.getBookByISBN(transaction.getIsbn());
            int quantity = transaction.getQuantity();
            boolean success = TransactionDAO.deleteTransaction(transaction.getId());
            boolean check = BookDAO.updateRemainingByISBN(book.getISBN(), quantity);

            if(success && check) {
                loadTransactions(IDsearchStudent.getText());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi trả sách: " + e.getMessage());
        }
    }

    /**
     * Tải thông tin sinh viên dựa trên mã sinh viên.
     * Nếu tìm thấy sinh viên, hiển thị thông tin của họ và các giao dịch của sinh viên.
     */
    private void loadStudentInfo() {
        String studentID = IDsearchStudent.getText();

        if (studentID != null) {
            try {
                User student = User.loadStudentDetailsByID(studentID);
                if (student != null) {
                    nameStudentTextfield.setText(student.getFullName());
                    studentEmailTextfield.setText(student.getEmail());
                    studentNumberTextfield.setText(student.getNumber());
                    loadTransactions(studentID);
                } else {
                    showAlbertDialog("Không tìm thấy sinh viên.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Tải danh sách giao dịch của sinh viên từ cơ sở dữ liệu và hiển thị trên bảng.
     *
     * @param studentID Mã sinh viên
     */
    private void loadTransactions(String studentID) {
        try {
            List<Transaction> transactions = TransactionDAO.getTransactionsByStudentId(studentID);
            transactionList.setAll(transactions);  // Tải các giao dịch vào bảng
        } catch (SQLException e) {
            showErrorDialog("Lỗi", e.getMessage());
        }
    }

    /**
     * Hiển thị hộp thoại lỗi.
     *
     * @param title   Tiêu đề của hộp thoại
     * @param message Thông báo lỗi cần hiển thị
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thông báo.
     *
     * @param message Thông báo cần hiển thị
     */
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
