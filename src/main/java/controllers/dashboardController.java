package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class dashboardController {

    @FXML
    private Label timeLabel;

    @FXML
    private Label usernameLabel;

    public void initialize() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    String currentTime = sdf.format(new Date());
                    timeLabel.setText(currentTime);
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
