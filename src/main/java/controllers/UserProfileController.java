package controllers;
import User.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UserProfileController {

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label idLabel;

    @FXML
    private TextField fullName;

    @FXML
    private TextField email;

    @FXML
    private TextField id;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private void initialize() {
        fullName.setText(currentUser.getFullName());
        System.out.println("Full name : " + currentUser.getFullName());
        email.setText(currentUser.getEmail());
        id.setText(String.valueOf(currentUser.getId()));
        username.setText(currentUser.getUsername());
        password.setText(currentUser.getPassword());
        fullNameLabel.setText(currentUser.getFullName());
        idLabel.setText(String.valueOf(currentUser.getId()));

    }
}
