/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough
 * Created: 03/17/2021
 */
package group3.views;

import group3.Transcript;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * A View that students view where they can upload transcripts, view upcoming courses for their Major, and any
 * prerequisites that classes have.
 */
public class StudentController {

    @FXML
    Pane viewPane;

    @FXML
    ChoiceBox viewSelection;

    /**
     * Find and add items to student view. Including Upcoming or Prerequisite courses
     */
    @FXML
    public void initialize() {
        viewSelection.getItems().add("Upcoming Courses");
        viewSelection.getItems().add("Prerequisite Courses");
        viewSelection.getItems().add("Graduation Plan");
        viewSelection.setOnAction(this::changeView);
    }

    /**
     * Change student view, so the student can look for upcoming courses, prerequisite course, or other views.
     * @param event event trigger view switch
     */
    private void changeView(Event event) {
        String target = viewSelection.getSelectionModel().getSelectedItem().toString();
        System.out.println(target);
        try {
            Parent view = FXMLLoader.load(getClass().getResource("student/" + target.replaceAll("\\s+", "") + ".fxml"));
            viewPane.getChildren().clear();
            viewPane.getChildren().add(view);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "The view you selected does not exist. Please report this.").showAndWait();
        }
    }

    /**
     * Imports the PDF when selected in the file menu
     */
    public void importPDF() {
        while(!Transcript.isImported()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load File");

            File file = new File(System.getProperty("user.home"));
            fileChooser.setInitialDirectory(file);
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("PDF's", "*.pdf");
            fileChooser.getExtensionFilters().add(filter);

            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File to import cannot be null");
                alert.showAndWait();
            } else {
                Transcript.load(selectedFile);
            }
        }
    }
}
