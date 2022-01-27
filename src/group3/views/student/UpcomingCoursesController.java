/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough
 * Created: 03/17/2021
 */

package group3.views.student;

import group3.Course;
import group3.Curriculum;
import group3.Prerequisite;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * Allows the Student to select their major and a term to see
 * the courses that are available to them in that term.
 * @author Kenneth McDonough
 * @version 2021.03.30
 */
public class UpcomingCoursesController {
    @FXML
    ChoiceBox majors, terms;

    @FXML
    BorderPane pane;

    @FXML
    ListView<Course> listView;

    /**
     * Loads a list of offered courses that will be running for the upcoming term for the students course.
     * Displaces them as a list on the UI.
     */
    @FXML
    public void initialize() {
        majors.getItems().add("Software Engineering");
        majors.getItems().add("Computer Science");

        majors.getSelectionModel().select(0);

        terms.getItems().add("Fall");
        terms.getItems().add("Winter");
        terms.getItems().add("Spring");

        terms.getSelectionModel().select(0);
    }

    /**
     * When a one of the ChoiceBoxes is changed, this method
     * is called. Gets choices, parses appropriately, and populates
     * the ListView with available courses.
     * @param e event, not used.
     */
    public void changed(ActionEvent e) {
        // The following lines are used to prevent null pointer exceptions
        // that occur in event calls during the population of the ChoiceBoxes
        if (majors.getSelectionModel().getSelectedItem() == null) return;
        if (terms.getSelectionModel().getSelectedItem() == null) return;

        Curriculum.Major major = getSelectedMajor();
        Curriculum.Term term = getSelectedTerm();

        // Should not happen, but prevents null pointer exceptions that may
        // occur if the ChoiceBox selections are invalid
        if (major == null || term == null) return;

        listView.getItems().clear();

        List<Course> offerings = Curriculum.getOfferings(major, term);
        for (Course c : offerings) {
            listView.getItems().add(c);
        }
    }

    private Curriculum.Major getSelectedMajor() {
        return Curriculum.stringToMajor(majors.getSelectionModel().getSelectedItem().toString());
    }
    private Curriculum.Term getSelectedTerm() {
        return Curriculum.stringToTerm(terms.getSelectionModel().getSelectedItem().toString());
    }

    @FXML
    private void onCourseSelected(MouseEvent e) {
        Course selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        createPopup(selected);
    }

    private void createPopup(Course course) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        BorderPane pane = new BorderPane();
        HBox hbox = new HBox();
        VBox left = new VBox();
        VBox right = new VBox();

        hbox.getChildren().addAll(left, right);

        left.setPrefSize(400, 600);
        right.setPrefSize(400, 600);
        hbox.setPrefSize(800, 600);

        left.setSpacing(5);

        left.setAlignment(Pos.CENTER);
        right.setAlignment(Pos.CENTER);

        left.getChildren().add(new Label("Prerequisites:"));

        if (course.getPrerequisites().isCourse()) {
            left.getChildren().add(new CoursePane(course.getPrerequisites().getCourse(), true));
        } else {
            for (Prerequisite p : course.getPrerequisites().getPrerequisites()) {
                if (p.isCourse()) {
                    left.getChildren().add(new CoursePane(p.getCourse(), true));
                }
            }
        }

        right.getChildren().add(new CoursePane(course));

        pane.setCenter(hbox);

        Scene scene = new Scene(pane, 800, 600);
        dialog.setScene(scene);
        dialog.setTitle("Prerequisites for " + course.getDescription());
        dialog.showAndWait();
    }
}
