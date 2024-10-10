package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.Connection;
public class adminMenuController implements Initializable {

    @FXML
    private Button logoutButton;
    @FXML
    private Button addBookButton;
    @FXML
    private Button viewMembersButton;
    @FXML
    private Button viewBooksButton;
    @FXML
    private Button addMemberButton;
    @FXML
    private Button viewOrdersButton;
    @FXML
    private Button onViewOrdersButtonClick;



    @FXML
    private void onAddMemberButtonClick(ActionEvent event) {

    }
    @FXML
    private void onViewMembersButtonClick(ActionEvent event) {
        // TODO: Implement the logic to view all members
    }
    @FXML

    private void onLogoutButtonClick(ActionEvent event) throws IOException {
        // TODO: Implement the logic to log out and return to the login screen
        if (event.getSource() == logoutButton) {
            System.out.println("logged out");
             Stage stage = (Stage) logoutButton.getScene().getWindow();
             stage.close();
             Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
             Scene scene = new Scene(root);
             stage.setScene(scene);
             stage.show();
        }

    }
    @FXML
    private void onViewBooksButtonClick(ActionEvent event) {
        // TODO: Implement the logic to view all books
    }
    @FXML
    private void onAddBookButtonClick(ActionEvent event) {
        // TODO: Implement the logic to add a new book
    }
    @FXML
    private void onViewOrdersButtonClick(ActionEvent event) {
        // TODO: Implement the logic to view all orders
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
