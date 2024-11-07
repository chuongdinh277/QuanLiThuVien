package controllers;

import Document.Transaction;
import Document.TransactionDAO;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Date;
import java.util.List;

public class allIssueBookController {

    @FXML
    private TableView<Transaction> transactionTable; // Khai báo TableView cho giao dịch
    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn; // Cột ID giao dịch
    @FXML
    private TableColumn<Transaction, String> isbnColumn; // Cột ISBN sách
    @FXML
    private TableColumn<Transaction, String> titleColumn; // Cột Tên sách
    @FXML
    private TableColumn<Transaction, String> authorColumn; // Cột Tác giả sách
    @FXML
    private TableColumn<Transaction, String> studentNameColumn; // Cột Tên sinh viên
    @FXML
    private TableColumn<Transaction, Integer> quantityColumn; // Cột Số lượng sách
    @FXML
    private TableColumn<Transaction, Date> borrowDateColumn; // Cột Ngày mượn
    @FXML
    private TableColumn<Transaction, Date> returnDateColumn; // Cột Ngày trả

    @FXML
    private void initialize() {
        // Cài đặt các giá trị hiển thị cho các cột trong TableView
        transactionIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        borrowDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrow_Date()));
        returnDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturn_Date()));

        // Gọi phương thức để load giao dịch vào TableView
        loadAllTransactions();
    }

    // Phương thức để load tất cả giao dịch từ cơ sở dữ liệu vào TableView
    private void loadAllTransactions() {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

        try {
            // Lấy tất cả giao dịch từ cơ sở dữ liệu
            List<Transaction> transactions = TransactionDAO.getAllTransaction();

            if (transactions != null) {
                transactionList.addAll(transactions); // Thêm tất cả giao dịch vào ObservableList
            } else {
                System.out.println("Không có giao dịch trong cơ sở dữ liệu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải giao dịch: " + e.getMessage());
        }

        // Đặt danh sách giao dịch vào TableView
        transactionTable.setItems(transactionList);
    }
}
