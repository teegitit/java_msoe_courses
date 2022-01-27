/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough, Austin Boley, Luke Miller, Thy Le
 * Created: 03/30/2021
 */

package group3;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Handles app startup, sets up initial UI, handles Student or Admin log in, then loads student/Admin UI views.
 */
public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        // Set up primary view
        VBox box = new VBox();

        Button student = new Button("I'm a Student");
        student.setOnAction(this::handleStudent);
        Button admin = new Button("I'm an Administrator");
        admin.setOnAction(this::handleAdmin);

        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);
        box.getChildren().addAll(student, admin);

        stage.setTitle("Start");
        stage.setScene(new Scene(box, 200, 100));
        stage.setResizable(false);
        stage.show();

        this.stage = stage;
    }

    private void handleAdmin(ActionEvent event) {
        stage.hide();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("views/admin.fxml"));
            stage.setTitle("Administrator View");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Could not load the program layout").show();
        }
    }

    private void handleStudent(ActionEvent event) {
        stage.hide();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("views/student.fxml"));
            stage.setTitle("Student View");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Could not load the program layout").show();
        }
    }

    public static void main(String[] args) {
        Curriculum.load();

        launch(args);
    }
}
