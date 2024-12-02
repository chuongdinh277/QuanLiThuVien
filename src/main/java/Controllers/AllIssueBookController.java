package Controllers;

import Document.Transaction;
import Document.TransactionDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Date;
import java.util.List;

public class AllIssueBookController {

    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn;
    @FXML
    private TableColumn<Transaction, String> isbnColumn;
    @FXML
    private TableColumn<Transaction, String> titleColumn;
    @FXML
    private TableColumn<Transaction, String> authorColumn;
    @FXML
    private TableColumn<Transaction, String> studentNameColumn;
    @FXML
    private TableColumn<Transaction, Integer> quantityColumn;
    @FXML
    private TableColumn<Transaction, Date> borrowDateColumn;
    @FXML
    private TableColumn<Transaction, Date> returnDateColumn;
    @FXML
    private TableColumn<Transaction, String> StudentID;

    /**
     * Khởi tạo các cột trong bảng và tải dữ liệu giao dịch vào bảng.
     */
    @FXML
    private void initialize() {
        transactionIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        StudentID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMssv()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        borrowDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrow_Date()));
        returnDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturn_Date()));

        loadAllTransactions();
    }

    /**
     * Tải tất cả các giao dịch từ cơ sở dữ liệu và hiển thị trong TableView.
     */
    private void loadAllTransactions() {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

        try {
            List<Transaction> transactions = TransactionDAO.getAllTransaction();

            if (transactions != null) {
                transactionList.addAll(transactions);
            } else {
                showAlbertDialog("Không có giao dịch trong cơ sở dữ liệu.");
            }
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải giao dịch: " + e.getMessage());
        }

        transactionTable.setItems(transactionList);
    }

    /**
     * Hiển thị hộp thoại lỗi với thông báo được cung cấp.
     *
     * @param message Thông báo lỗi cần hiển thị.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại thông tin với thông báo được cung cấp.
     *
     * @param message Thông báo cần hiển thị.
     */
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
