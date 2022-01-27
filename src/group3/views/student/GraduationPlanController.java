/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Thy Le
 * Created: 04/12/2021
 */
package group3.views.student;

import group3.Course;
import group3.Curriculum;
import group3.Transcript;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GraduationPlanController {
    @FXML
    private ChoiceBox major;

    @FXML
    private Button output;

    @FXML
    private ListView<HBox> list;

    @FXML
    private Label gradDate;

    @FXML
    private Label electiveFor;

    @FXML
    private TextArea electiveTextArea;

    @FXML
    private Button updateButton;

    private List<Course> year1;
    private List<Course> year2;
    private List<Course> year3;
    private List<Course> year4;
    private HashMap<String, String> taken; //Taken and passed
    private List<String> elecCourses;
    private int COURSES_PER_YEAR = 14;

    /**
     * Initializes the UI of the feature
     */
    @FXML
    public void initialize() {
        major.getItems().add("Software Engineering");
        major.getItems().add("Computer Science");
        major.getSelectionModel().select(0);
    }

    /**
     * Reads and add all the courses taken based on the transcript
     */
    private void addTakenCourse(){
        taken = new HashMap<>();
        List<String> coursesTaken = Transcript.getCourses();
        for(String c : coursesTaken){
            String code = c.split(" ")[0];
            String grade = c.split(" ")[1];
            taken.put(code, grade);
        }
    }

    /**
     * Loads the elective courses into a list
     * @param major the major on the transcript/choice box
     * @throws IOException if error occurs while loading the offerings file
     */
    private void loadElective(Curriculum.Major major) throws IOException{
        elecCourses = new ArrayList<>();
        List<String> allCourses = loadAllCourse();
        List<Course> required = Curriculum.getCurriculum(major);
        List<String> requiredString = new ArrayList<>();
        for(Course r: required){
            requiredString.add(r.getCourseCode());
        }
        for(String c: allCourses){
            if(!requiredString.contains(c) && !c.contains("CS40") && !c.contains("SE40")){
                elecCourses.add(c);
            }
        }
    }

    /**
     * Load all the courses offered at MSOE
     * @return the list of all the course
     * @throws IOException if error occurs while loading the offerings file
     */
    private List<String> loadAllCourse() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/group3/offerings.csv"));
        List<String> all = new ArrayList<>();
        for(int i = 1; i < lines.size(); i++){
            String code = lines.get(i).split(",")[0];
            all.add(code);
        }
        return all;
    }

    /**
     * Loads and update the list view
     * @param major the major on the transcipt/choice box
     * @throws IOException if error occurs while loading elective courses
     */
    private void load(Curriculum.Major major) throws IOException{
        List<Course> courses = Curriculum.getCurriculum(major);
        List<String> electives = Arrays.asList("SCIEL", "MASCIEL", "BUSEL", "HUSS", "FREE", "TECHEL");
        ObservableList<HBox> observableList = list.getItems();
        addTakenCourse();
        loadElective(major);
        for(Course c: courses){
            String pass = "";
            String name = "";
            String date = "";
            if(Transcript.isImported()){
                if(taken.containsKey(c.getCourseCode())){
                    pass = taken.get(c.getCourseCode());
                }
            }
            if(!electives.contains(c.getCourseCode())){
                if(c.getPrerequisites() != null && c.getPrerequisites().toString().length() != 0){
                    String preq = c.getPrerequisites().toString().replaceAll("\\p{P}","");
                    name = c.getCourseCode() + ": " + c.getDescription() + "\n[Prereq: " +  preq + "]";
                } else{
                    name = c.getCourseCode() + ": " + c.getDescription() + "\n[Prereq: none]";
                }
                date = getTerm(major, c);
                setContent(new Label(name), new Label(date), new Label(pass));
            } else if(electives.contains(c.getCourseCode())){
                String elec = c.getCourseCode();
                String type = "Potential decision";
                String grade = "";
                setContent(new Label(elec), new Label(type), new Label(grade));
                for (String code : taken.keySet()) {
                    Course course = Curriculum.getCourse(code);
                    if (elecCourses.contains(code) && c.getCourseCode().equals(getElectiveType(code))) {
                        elec = code + ": " + course.getDescription() + "\n[Prereq: " + course.getPrerequisites() + "]";
                        type = "Taken as " + c.getCourseCode();
                        grade = "Pass";
                        HBox box = new HBox();
                        Label electiveName = new Label(elec);
                        electiveName.setPrefWidth(320);
                        Label elecType = new Label(type);
                        elecType.setPrefWidth(100);
                        Label elecGrade = new Label(grade);
                        elecGrade.setPrefWidth(80);
                        box.getChildren().addAll(electiveName, elecType, elecGrade);
                        list.getItems().add(box);
                        observableList.remove(observableList.indexOf(box) - 1);
                        elecCourses.remove(code);
                    }
                }
            }
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        System.out.println(courses.size() - taken.size());
        int estimate = Math.round((float)(courses.size() - taken.size())/COURSES_PER_YEAR);
        gradDate.setText("Estimated graduation date: " + (currentYear + estimate));
    }

    /**
     * Gets the term the course if offered
     * @param major the major that the course belongs to
     * @param c the course to look for the term
     * @return the string of the term
     */
    private String getTerm(Curriculum.Major major, Course c){
        List<Course> fall = Curriculum.getOfferings(major, Curriculum.Term.Fall);
        List<Course> winter = Curriculum.getOfferings(major, Curriculum.Term.Winter);
        List<Course> spring = Curriculum.getOfferings(major, Curriculum.Term.Spring);
        String date = "";
        if(fall.contains(c)){
            date = "Fall of " + courseToYear(c);
        } else if(winter.contains(c)){
            date = "Winter of " + courseToYear(c);
        } else if(spring.contains(c)){
            date = "Spring of " + courseToYear(c);
        } else{
            date = "Potential decision";
        }
        return date;

    }

    /**
     * Gets the type of electives of the course
     * @param parameterCode the course code to be determined
     * @return the string of the elective type (department)
     */
    private String getElectiveType(String parameterCode){
        Course c = Curriculum.getCourse(parameterCode);
        String code = c.getCourseCode();
        int credit = c.getCredits();
        if(code.contains("CH") || code.contains("PH") || code.contains("BI")){
            if(credit == 3){
                return "MASCIEL";
            } else if(credit == 4){
                return "SCIEL";
            }
        } else if(code.contains("MA")){
            return "MASCIEL";
        } else if(code.contains("BA")){
            return "BUSEL";
        } else if(code.contains("HU") || code.contains("SS")){
            return "HUSS";
        } else if(code.contains("CS") || code.contains("SE") || code.contains("TC")){
            return "TECHEL";
        } else{
            return "FREE";
        }
        return "";
    }

    /**
     * Gets the list of elective courses based on the type of elective
     * @param type the type of the electives (department)
     * @return the list of all elective courses
     */
    private List<String> getElectiveList(String type){
        List<String> masci = new ArrayList<>();
        List<String> tech = new ArrayList<>();
        List<String> sci = new ArrayList<>();
        List<String> free = new ArrayList<>();
        List<String> huss = new ArrayList<>();
        List<String> bus = new ArrayList<>();
        for(String e: elecCourses){
            if(e.contains("MA")){
                masci.add(e);
            } else if(e.contains("CH") || e.contains("PH") || e.contains("BI")){
                sci.add(e);
            } else if(e.contains("TC") || e.contains("SE") || e.contains("CS")){
                tech.add(e);
            } else if(e.contains("HU") || e.contains("SS")){
                huss.add(e);
            } else if(e.contains("BA")){
                bus.add(e);
            } else{
                free.add(e);
            }
        }
        if(type.equals("MASCIEL")) return masci;
        if(type.equals("TECHEL")) return tech;
        if(type.equals("SCIEL")) return sci;
        if(type.equals("FREE")) return free;
        if(type.equals("HUSS")) return huss;
        if(type.equals("BUSEL")) return bus;
        return null;
    }

    /**
     * Sets the content for each box on the list view
     * @param l1 the course name label
     * @param l2 the date offered label
     * @param l3 the grade status label
     */
    private void setContent(Label l1, Label l2, Label l3){
        l1.setPrefWidth(320);
        l2.setPrefWidth(100);
        l3.setPrefWidth(80);
        HBox box = new HBox();
        box.getChildren().addAll(l1, l2, l3);
        list.getItems().add(box);
    }

    /**
     * Loads course into the year according to the track
     * @param major the track's major
     */
    private void loadYear(Curriculum.Major major){
        int difference = 0;
        if(major == Curriculum.Major.SE){
            difference = 41;
        } else{
            difference = 39;
        }
        List<Course> courses = Curriculum.getCurriculum(major);
        year1 = new ArrayList<>();
        year2 = new ArrayList<>();
        year3 = new ArrayList<>();
        year4 = new ArrayList<>();

        for(int i = 0; i < courses.size(); i++){
            if(i < 12){
                year1.add(courses.get(i));
            } else if(i >= 12 && i < 26){
                year2.add(courses.get(i));
            } else if(i >= 26 && i < difference){
                year3.add(courses.get(i));
            } else{
                year4.add(courses.get(i));
            }
        }
    }

    /**
     * Determine which year the course is offered
     * @param course the course to be determined
     * @return the string of year the course if offered on the track
     */
    private String courseToYear(Course course){
        String yearReturn = "";
        if(year1.contains(course)){
            yearReturn = "Year 1";
        } else if(year2.contains(course)){
            yearReturn = "Year 2";
        } else if(year3.contains(course)){
            yearReturn = "Year 3";
        } else{
            yearReturn = "Year 4";
        }
        return yearReturn;
    }

    /**
     * Handles the event of choosing the major choice box
     * @throws IOException throw if error occurs while loading elective courses
     */
    @FXML
    public void update() throws IOException {
        list.getItems().clear();
        updateButton.setDisable(false);
        if (major.getSelectionModel().getSelectedItem() == null) return;
        if(Transcript.isImported()){
            if(Transcript.getMajor().equals("SE")){
                major.getSelectionModel().select(0);
                major.getItems().remove(1);
            }
            if(Transcript.getMajor().equals("CS")){
                major.getSelectionModel().select(1);
                major.getItems().remove(0);
            }
            updateButton.setDisable(true);
            output.setDisable(false);

        }else{
            updateButton.setDisable(false);
            output.setDisable(true);
        }
        String target = major.getSelectionModel().getSelectedItem().toString();
        loadYear(Curriculum.stringToMajor(target));
        load(Curriculum.stringToMajor(target));
    }

    /**
     * Handles clicking on a cell of a list view
     * @param mouseEvent the event of clicking on the cell
     */
    @FXML
    public void handleClickedCell(MouseEvent mouseEvent) {
        electiveTextArea.clear();
        HBox selectedBox = list.getSelectionModel().getSelectedItem();
        if(selectedBox != null){
            String courseName = ((Label)selectedBox.getChildren().get(0)).getText();
            if(courseName.contains(":")) courseName = courseName.split(":")[0];
            String dateTaken = ((Label) selectedBox.getChildren().get(1)).getText();
            if(dateTaken.contains("Taken as")){
                dateTaken = dateTaken.split("Taken as ")[1];
            }
            List<String> electives = Arrays.asList("SCIEL", "MASCIEL", "BUSEL", "HUSS", "FREE", "TECHEL");

            String txt = "";
            String type = "";
            String str = "";
            if(electives.contains(dateTaken)){
                electiveFor.setText("Elective courses for: " + dateTaken);
                type = dateTaken;
            }
            if(electives.contains(courseName)){
                electiveFor.setText("Elective courses for: " + courseName);
                type = courseName;
            }
            List<String> toShow = getElectiveList(type);
            if(toShow != null){
                for(String s: toShow){
                    Course course = Curriculum.getCourse(s);
                    String preq = "";
                    if(course != null){
                        if(course.getPrerequisites() != null && course.getPrerequisites().toString().length() != 0){
                            preq = course.getPrerequisites().toString();
                        } else{
                            preq = "none";
                        }
                        str = s + ": " + course.getDescription() + "\n[Prereq: " + preq + "]\n";
                        txt += "\n" + str;
                    } else{
                        txt += "\n" + s + ": Information not found\n";
                    }
                    electiveTextArea.setText(txt);
                }
            }
        }
    }

    /**
     * Output the graduation plan in the text area
     * @param actionEvent The event of clicking on the output button
     */
    @FXML
    public void outputGradPlan(ActionEvent actionEvent) {
        electiveFor.setText("");
        electiveTextArea.clear();
        String txt = "";
        txt += gradDate.getText() + "\n";
        ObservableList<HBox> observableList = list.getItems();
        List<String> notTakenYet = new ArrayList<>();
        for(HBox b : observableList){
            String course = ((Label)b.getChildren().get(0)).getText();
            course = course.split(":")[0];
            String grade = ((Label)b.getChildren().get(2)).getText();
            if(grade.length() == 0){
                notTakenYet.add(course);
            }
        }
        for(String code: notTakenYet){
            try{
                Course c = Curriculum.getCourse(code);
                String m = Transcript.getMajor();
                if(c != null){
                    String date = getTerm(Curriculum.stringToMajor(m), c);
                    String pre = "";
                    if(c.getPrerequisites() != null && c.getPrerequisites().toString().length() != 0){
                        pre  = "[Prereq: " + c.getPrerequisites().toString().replaceAll("\\p{P}", "") + "]\n";
                    }else{
                        pre = "[Prereq: none]" + "\n";
                    }
                    txt += "\n" + c.getCourseCode() + ": " + c.getDescription()
                            + "\n" + pre + date + "\n";
                }else {
                    txt += "\n" + code + ": Potential decision\n";
                }
            }catch(IllegalArgumentException ex){
                //ignore
            }
        }
        electiveTextArea.setText(txt);
    }
}