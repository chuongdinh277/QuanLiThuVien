package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent; // Correct import

import java.net.URL;
import java.util.ResourceBundle;


public class HelloController implements Initializable {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordText;

    @FXML
    private PasswordField passwordHidden;

    @FXML
    private CheckBox checkBox;

    @FXML
    private TextField passwordField;
    //@FXML
    void change(ActionEvent event) {
        if (checkBox.isSelected()) {
            System.out.println("Check");
            passwordText.setText(passwordHidden.getText());
            passwordHidden.setVisible(false);
            passwordText.setVisible(true);
            return;
        }
        passwordHidden.setText(passwordText.getText());
        passwordHidden.setVisible(true);
        passwordText.setVisible(false);
    }


    @FXML ChoiceBox<String> myChoiceBox;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myChoiceBox.getItems().add("Quản trị viên");
        myChoiceBox.getItems().add("Người dùng");
    }


}
