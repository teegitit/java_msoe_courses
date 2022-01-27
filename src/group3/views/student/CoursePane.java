/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough
 * Created: 05/10/2021
 */
package group3.views.student;

import group3.Course;
import group3.Curriculum;
import group3.Transcript;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * A special pane to hold a course
 * @author Kenneth McDonough
 * @version 5/10/2021
 */
public class CoursePane extends Pane {
    public CoursePane(Course course) {
        this(course, false);
    }

    public CoursePane(Course course, boolean fulfilled) {

        course = Curriculum.getCourse(course.getCourseCode());
        String color;
        if (fulfilled) {
            if (!Transcript.isImported()) {
                fulfilled = false;
            } else {
                fulfilled = course.getPrerequisites().isSatisfied() && Transcript.getCourseGradeStatus(course.getCourseCode());
            }
            color = fulfilled ? "green" : "red";
        } else {
            color = "black";
        }

        setStyle("-fx-border-color: " + color + "; -fx-border-width: 2px; -fx-max-width: 250px; -fx-max-height: 50px; -fx-min-width: 250px; -fx-min-height: 50px;");

        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(course.getCourseCode() + ": " + course.getDescription()));
        if (!color.equals("black")) vbox.getChildren().add(new Label(fulfilled ? "Taken" : "Need to take"));

        getChildren().add(vbox);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.setAlignment(Pos.CENTER);
    }
}
