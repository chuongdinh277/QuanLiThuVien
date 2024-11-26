package controllers;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class returnBookController {

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

    @FXML
    private void initialize() {
        IDsearchStudent.setOnAction(event -> loadStudentInfo());

        // Configure columns
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
                // Add an action to the button
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
        // Link the list to the tablen
        transactionTable.setItems(transactionList);
    }

    private void handleReturnBook(Transaction transaction) {
        // Xử lý sự kiện
        try {
            Book book = BookDAO.getBookByISBN(transaction.getIsbn());
            int quantity = transaction.getQuantity();
            boolean success = TransactionDAO.deleteTransaction(transaction.getId());
            boolean check = BookDAO.updateRemainingByISBN(book.getISBN(), quantity);

            if(success && check) {
                loadTransactions(IDsearchStudent.getText());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error occurred while returning the book: " + e.getMessage());
        }
    }

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
                    showAlbertDialog("Student not found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadTransactions(String studentID) {
        try {
            List<Transaction> transactions = TransactionDAO.getTransactionsByStudentId(studentID);
            transactionList.setAll(transactions);  // Load transactions into the table view
        } catch (SQLException e) {
            showErrorDialog("Error", e.getMessage());
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
