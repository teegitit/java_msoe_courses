/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Austin Boley
 * Created: 03/20/2021
 */

package group3;

import javafx.scene.control.Alert;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import group3.Curriculum.Term;

/**
 * The Transcript class, Takes in a pdf version of the transcript and can return if a class was taken or not
 * and the grade received in the class (Pass, Fail, or WIP)
 */
public class Transcript {
    private static HashMap<String, String> courses = new HashMap<>();
    private static ArrayList<String> coursesTaken = new ArrayList<>();
    private static LinkedHashMap<Course, String> transcript = new LinkedHashMap<>();
    private static String parsedText, major, majorStatus;
    private static Curriculum.Term term;

    /**
     * Used to populate the hash so we know what has been taken based on the transcript imported
     * @param file the transcript
     */
    public static void load(File file){
        courses.clear();
        coursesTaken.clear();
        major = "";
        parsedText = "";
        majorStatus = "";
        if (file != null) {
            readPDF(file);
            populateHash();
        }
    }

    /**
     * The main implementation of the reading in the pdf, and prints it to the console
     *
     * @param file the transcript that is being read in
     */
    private static void readPDF(File file) {
        try {
            PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(file));
            parser.parse();
            COSDocument cosDoc = parser.getDocument();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            PDDocument pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            parsedText = pdfStripper.getText(pdDoc);
            pdDoc.close();
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error while reading the pdf that was selected");
            alert.showAndWait();
        }
    }

    /**
     * This will return the transcript data back so that it only contains the course info
     * this includes the course code, course name, and grade received
     *
     * @return an array list of the courses taken from the transcript
     */
    private static ArrayList<String> editTranscript() {
        String[] separated = parsedText.split("\\r?\\n");
        ArrayList<String> transcriptEdited = new ArrayList<>();

        for (String s : separated) {
            if (s.contains("Software Engineer")) {
                major = "SE";
            } else if (s.contains("Computer Science")) {
                major = "CS";
            } else if (s.contains("Quarter") || s.contains("Year")) {
                if (s.contains("Fall")) {
                    term = Term.Fall;
                } else if (s.contains("Winter")) {
                    term = Term.Winter;
                } else if (s.contains("Spring")) {
                    term = Term.Spring;
                }
            }

            if (s.matches("^([A-Z]{2}\\d{3,4}\\w?)(.*)")) {
                // Checks that the line starts with any two letters, followed by 3 or 4 numbers. (AA2020, course code)
                transcriptEdited.add(s);
            }
        }
        return transcriptEdited;
    }

    /**
     * This returns the grade of the course taken and if was Passed, Failed, or WIP
     *
     * @param string the grade on the transcript
     * @return the gradd but changed to either pass or fail
     */
    private static String editGrades(String string) {
        if (string.contains("A") || string.contains("B") || string.contains("C") || string.contains("D") || (string.contains("P") && string.length() != 3) || string.contains("RF") || string.contains("TR")) {
            return "Pass";
        } else if (string.contains("WIP")) {
            return string;
        } else {
            return "Fail";
        }
    }

    /**
     * This will add the courses to the hash to be used elsewhere
     */
    private static void populateHash() {
        ArrayList<String> transcript = editTranscript();

        for (String s : transcript) {
            String[] separated = s.split(" ");
            String courseCode = separated[0];
            if (!separated[separated.length-1].contains("RF")) {
                String grade = editGrades(separated[separated.length - 1].replaceAll("[*0-9]", "").replace(".", ""));
                coursesTaken.add(courseCode + " " + grade);
                courses.put(courseCode, grade);
            }
        }

        if(coursesTaken.size() == 0 || courses.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The pdf imported had no valid courses try a different file");
            alert.showAndWait();
        }

    }

    /**
     * Returns if the course exists or not in the transcript
     *
     * @param courseCode the course being checked for
     * @return if the course was taken
     */
    public static boolean getCourseTaken(String courseCode) {
        return courses.containsKey(courseCode);
    }

    /**
     * Returns whether the student has passed the course or not
     *
     * @param courseCode the code of the course trying to retrieve the grade of
     * @return if the student has passed or not
     */
    public static boolean getCourseGradeStatus(String courseCode) {
        if (courses.get(courseCode) == null) return false;
        return courses.get(courseCode).equalsIgnoreCase("pass");
    }

    /**
     * Allows the user to check if the Transcript was imported
     *
     * @return true if it was and false it it was not
     */
    public static boolean isImported() {
        return courses.size() > 0;
    }

    /**
     * Returns the Students Major
     *
     * @return the major type
     */
    public static String getMajor() {
        return major;
    }

    /**
     * Returns the term specified in the transcript, Will save the last one to it
     * @return the term
     */
    public static Term getTerm() {
        return term;
    }

    /**
     * Set the term to what is being passed in
     * @param t the term being passed in
     */
    public static void setTerm(Term t) {
        term = t;
    }

    /**
     * This will export the course code and grade received for the class
     * Used Only by Admin
     *
     * @return transcriptExport, the transcript the Admin created
     */
    public static LinkedHashMap<Course, String> exportPDF() {
        if (transcript.size() == 0) {
            return null;
        } else {
            return transcript;
        }
    }

    /**
     * Only used in the Admin side of things since they are creating a transcript
     *
     * @param script the Hashmap created for the Admins
     */
    public static void addCourse(LinkedHashMap<Course, String> script) {
        transcript = script;
    }

    /**
     * Used to return the courses that were taken in an array list
     * Format of COURSECODE GRADE
     * @return the array list of courses taken
     */
    public static ArrayList<String> getCourses() {
        return coursesTaken;
    }
}
