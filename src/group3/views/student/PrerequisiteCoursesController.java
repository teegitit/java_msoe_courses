/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Thy Le
 * Created: 03/19/2021
 */
package group3.views.student;
import group3.Course;
import group3.Curriculum;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * View Prerequisite Courses for the courses, Add, modify, and remove requisite course descriptions for an class
 */
public class PrerequisiteCoursesController {
    @FXML
    private Pane pane;

    private ListView<String> list;
    private List<String> courses = new ArrayList<>();
    private List<String> match;
    private TextField search;
    private int WIDTH = 500;
    private int HEIGHT = 550;
    private int SPACE = 10;

    /**
     * Initialize view of courses and theirs prerequisites
     * Include text field as search box for user to search course
     */
    @FXML
    public void initialize() {
        VBox box = new VBox();
        box.setPrefWidth(WIDTH);
        box.setSpacing(SPACE);

        search = new TextField();
        search.setPromptText("Enter a code or name of course");
        search.setPrefWidth(WIDTH);
        search.setOnKeyReleased(this :: searchCourse);

        list = new ListView<>();
        list.setPrefWidth(WIDTH);
        list.setPrefHeight(HEIGHT);
        box.getChildren().addAll(search, list);
        pane.getChildren().add(box);
        list.getItems().add("Prerequisites for:");

        Curriculum.Term[] terms = new Curriculum.Term[] {Curriculum.Term.Fall, Curriculum.Term.Winter, Curriculum.Term.Spring};
        load(Curriculum.Major.SE, terms);
        load(Curriculum.Major.CS, terms);
        courses.sort(Comparator.naturalOrder());
    }

    /**
     * Update list view results for course searching
     * @param actionEvent pressing any key to search course
     */
    private void searchCourse(KeyEvent actionEvent) {
        List<String> matchCourses = allThatBeginsWith(search.getText());
        list.getItems().clear();
        if(matchCourses.size() == 0){
            list.getItems().add("No matching courses found!");
        } else{
            list.getItems().clear();
            list.getItems().add("Prerequisites for: ");
            for(String s : matchCourses){
                list.getItems().addAll(s);
            }
        }
    }

    /**
     * Load all available courses of all term
     * @param major include courses
     * @param terms all term in an academic year
     */
    private void load(Curriculum.Major major, Curriculum.Term[] terms){
        for (Curriculum.Term term : terms) {
            for (Course course : Curriculum.getOfferings(major, term)) {
                String preq = course.getPrerequisites().toString().replaceAll("\\p{P}","");
                if(preq.length() == 0){
                    preq = "none";
                }
                list.getItems().add(course.getCourseCode() + ": " + course.getDescription() + " [" + preq + "]");
                courses.add(course.getCourseCode() + ": " + course.getDescription());
            }
        }
    }

    /**
     * Filter out courses that match what user searches for
     * @param prefix the key that users presses to search
     * @return the list contains all matching courses
     */
    private List<String> allThatBeginsWith(String prefix){
        match = new ArrayList<>();
        for(String c: courses){
            if(prefix.length() <= c.length()){
                prefix = prefix.toLowerCase().replaceAll(" ", "");
                prefix = prefix.replaceAll("\\p{P}","");
                if(c.toLowerCase().replaceAll(" ", "").contains(prefix)){
                    String separateCourse = c.split(":")[0];
                    Course course = Curriculum.getCourse(separateCourse);
                    String preq = course.getPrerequisites().toString().replaceAll("\\p{P}","");
                    if(preq.length() == 0){
                        preq = "none";
                    }
                    String courseString = separateCourse + ": " + course.getDescription() + " [" + preq + "]";
                    if(!(match.contains(courseString))){
                        match.add(courseString);
                    }
                }
            }
        }
        return match;
    }


}
