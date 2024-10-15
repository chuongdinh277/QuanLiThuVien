package controllers;

import Document.*;
import User.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class userViewController {
    @FXML
    private BorderPane borderPane_User;
    @FXML
    private Button bookLibrary;
    @FXML
    private Button bookBorrowed;
    @FXML
    private Button personInformation;
    @FXML
    private Button logout;
    @FXML
    private Button returnBook;

    @FXML
    private void bookLibrary_Button () {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/borrowBook.fxml"));
            Parent root = loader.load();
            borderPane_User.setCenter(root);
            System.out.println("tải thành công");
        } catch (Exception e) {
            System.out.println("lỗi không in được ảnh");
        }

    }
    @FXML
    private void bookBorrowed_Button ( ) {

    }
    @FXML
    private void personInformation_Button ( ) {
    }

    @FXML
    private void logout_Button (ActionEvent event) {
        currentUser.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {


        }
    }

}
