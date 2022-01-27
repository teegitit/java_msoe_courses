/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Austin Boley
 * Created: 03/20/2021
 */

package group3.views.admin;

import group3.Course;
import group3.Curriculum;
import group3.Transcript;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;

import group3.Curriculum.Term;

/**
 * The Controller for the the admin so that they can create a transcript
 * all based on the courses given from curriculum
 */
public class SelectCoursesController {
    @FXML
    ListView<Course> pane, trans;
    @FXML
    ListView<String> des;
    @FXML
    ChoiceBox<String> majors, terms, grade;
    @FXML
    Button add, remove;
    @FXML
    Label status;


    private LinkedHashMap<Course, String> transcriptU = new LinkedHashMap<>();
    private Course course;


    /**
     * Loads a list of offered courses for the selected term.
     * Displays them as a list on the UI.
     */
    @FXML
    public void initialize() {
        majors.getItems().add("Software Engineering");
        majors.getItems().add("Computer Science");

        majors.getSelectionModel().select(0);

        terms.getItems().add("All");
        terms.getItems().add("Fall");
        terms.getItems().add("Spring");
        terms.getItems().add("Winter");

        terms.getSelectionModel().select(0);

        grade.getItems().add("A");
        grade.getItems().add("AB");
        grade.getItems().add("B");
        grade.getItems().add("BC");
        grade.getItems().add("C");
        grade.getItems().add("CD");
        grade.getItems().add("D");
        grade.getItems().add("F");
        grade.getItems().add("WIP");
        grade.getItems().add("RF");
        grade.getItems().add("CR");

        grade.getSelectionModel().select(8);

        pane.setOnMouseClicked(this::onClick);
        trans.setOnMouseClicked(this::onClick);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This program is set up so that the programs you add are added in order, If one is removed it will make the next in line fill in that spot." +
                                                             " In addition This will produce an inaccurate transcript because some electives credits can vary in credit amounts since it can be either 3 or 4. We just say 0.");
        alert.showAndWait();
    }

    /**
     * This will handle any major change or term change so that
     * it displays the correct courses
     */
    public void changed() {
        if (majors.getSelectionModel().getSelectedItem() == null) return;
        if (terms.getSelectionModel().getSelectedItem() == null) return;

        Curriculum.Major major = Curriculum.stringToMajor(majors.getSelectionModel().getSelectedItem());
        Curriculum.Term term;
        if (terms.getSelectionModel().getSelectedItem().equals("All")) {
            term = Term.Fall;
        } else {
            term = Curriculum.stringToTerm(terms.getSelectionModel().getSelectedItem());
        }

        if (major == null || term == null) return;

        pane.getItems().clear();

        if (terms.getSelectionModel().getSelectedItem().equals("All")) {
            List<Course> offerings = Curriculum.getCurriculum(major);
            for (Course c : offerings) {
                pane.getItems().add(c);
            }
        } else {
            List<Course> offerings = Curriculum.getOfferings(major, term);
            for (Course c : offerings) {
                pane.getItems().add(c);
            }
        }

    }

    /**
     * The action event for pane so it can display the info for the selected course
     * @param e the action event itself
     */
    private void onClick(MouseEvent e) {
        if (e.getSource() instanceof ListView) {
            des.getItems().clear();

            ListView<Course> listView = (ListView<Course>) e.getSource();

            if (listView.getSelectionModel().getSelectedItem() != null) {
                course = listView.getSelectionModel().getSelectedItem();
                des.getItems().add("Course Code: " + listView.getSelectionModel().getSelectedItem().getCourseCode());
                des.getItems().add("Course Description: " + listView.getSelectionModel().getSelectedItem().getDescription());
                des.getItems().add("Course Credits: " + listView.getSelectionModel().getSelectedItem().getCredits());
                des.getItems().add("Prerequisites: " + listView.getSelectionModel().getSelectedItem().getPrerequisites());
                if (transcriptU.containsKey(listView.getSelectionModel().getSelectedItem())) {
                    des.getItems().add("Grade: " + transcriptU.get(listView.getSelectionModel().getSelectedItem()));
                }
            }
        }
    }

    /**
     * Adds the course to the trans ListView so that it can be accessed
     */
    public void addCourse() {
        if (course != null && !transcriptU.containsKey(course)) {
            transcriptU.put(course, grade.getSelectionModel().getSelectedItem());
            trans.getItems().add(course);
        }
    }

    /**
     * Removes the course from the trans and transcriptU
     */
    public void removeCourse() {
        if (course != null && transcriptU.containsKey(course)) {
            transcriptU.remove(course);
            trans.getItems().remove(course);
        }
    }

    /**
     * Returns the transcriptU HashMap so that it can be used in Transcript
     */
    public void save() {
        if (transcriptU.size() != 0) {
            Transcript.addCourse(transcriptU);
            status.setText("Saved");
        }
    }
}