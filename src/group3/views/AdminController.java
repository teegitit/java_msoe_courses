/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Austin Boley
 * Created: 03/17/2021
 */
package group3.views;

import group3.Course;
import group3.Transcript;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static javafx.scene.control.Alert.AlertType.ERROR;

/**
 * Controller to display the unique UI that the administrator of a program would use to view and work with student
 * Transcripts, Including unique features that are exclusive to administrators.
 */
public class AdminController {

    @FXML
    Pane viewPane;
    @FXML
    ChoiceBox<String> viewSelection;

    /**
     * Find and add items to admin view.
     */
    @FXML
    public void initialize() {
        viewSelection.getItems().add("Select Courses");
        viewSelection.getItems().add("Errors During Import");
        viewSelection.setOnAction(this::changeView);
    }

    /**
     * Change student view, so the student can look for upcoming courses, prerequisite course, or other views.
     *
     * @param event event trigger view switch
     */
    private void changeView(Event event) {
        String target = viewSelection.getSelectionModel().getSelectedItem();
        try {
            Parent view = FXMLLoader.load(getClass().getResource("admin/" + target.replaceAll("\\s+", "") + ".fxml"));
            viewPane.getChildren().clear();
            viewPane.getChildren().add(view);
        } catch (IOException ex) {
            Alert alert = new Alert(ERROR, "There was an issue changing the view to the new pane / feature");
            alert.showAndWait();
        }
    }

    /**
     * Just populates an Arraylist for export just used to clean up export
     *
     * @return the trimesters used
     */
    private ArrayList<String> populateTris() {
        ArrayList<String> listy = new ArrayList<>();
        listy.add("Fall Year 1");
        listy.add("Winter Year 1");
        listy.add("Spring Year 1");
        listy.add("Fall Year 2");
        listy.add("Winter Year 2");
        listy.add("Spring Year 2");
        listy.add("Fall Year 3");
        listy.add("Winter Year 3");
        listy.add("Spring Year 3");
        listy.add("Fall Year 4");
        listy.add("Winter Year 4");
        listy.add("Spring Year 4");
        listy.add("Fall Year 5");
        listy.add("Winter Year 5");
        listy.add("Spring Year 5");
        return listy;
    }

    /**
     * Exports a PDF of a mock Transcript an admin made
     */
    public void exportPDF() {
        try {
            if (Transcript.exportPDF() != null) {
                // Create a document and add a page to it
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);

                // Create a new font object selecting one of the PDF base fonts
                PDFont font = PDType1Font.HELVETICA_BOLD;

                // Start a new content stream which will "hold" the to be created content
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                PDRectangle mediaBox = page.getMediaBox();
                int fontSize = 12;
                int margin = 72;
                float leading = 1.5f * fontSize;
                float startX = mediaBox.getLowerLeftX() + margin;
                float startY = mediaBox.getUpperRightY() - margin;


                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(startX, startY);

                LinkedHashMap<Course, String> transcript = Transcript.exportPDF();
                ArrayList<String> listy = new ArrayList<>();
                ArrayList<String> trimesters = populateTris();
                int termSize = 0;
                int iteration = 0;
                int termTotal = 0;
                int totalCredits = 0;

                for (HashMap.Entry<Course, String> entry : transcript.entrySet()) {
                    if (termTotal >= 15 && iteration <= 11 || termSize == 5 || iteration == 0) {
                        if (iteration != 0) {
                            listy.add("Term Credits: " + termTotal);
                            listy.add("Total Credits: " + totalCredits);
                            listy.add("**************************************");
                            termTotal = 0;
                            termSize = 0;
                        }
                        listy.add(trimesters.get(iteration));
                        iteration++;
                    }
                    String course;
                    if (entry.getKey().getDescription().equals("SCIEL")) {
                        course = entry.getKey().getCourseCode() + " " + entry.getKey().getDescription() + "(Lab)" + " " + 4 + entry.getValue();
                        termTotal += 4;
                        totalCredits += 4;
                    } else {
                        course = entry.getKey().getCourseCode() + " " + entry.getKey().getDescription() + " " + entry.getKey().getCredits() + entry.getValue();
                        termTotal += entry.getKey().getCredits();
                        totalCredits += entry.getKey().getCredits();
                    }
                    listy.add(course);
                    termSize++;
                }

                listy.add("Term Credits: " + termTotal);
                listy.add("Total Credits: " + totalCredits);
                listy.add("**************************************");

                for (int i = 0; i < listy.size(); i++) {
                    if (i % 35 == 0 && i > 30) {
                        contentStream.endText();
                        contentStream.close();
                        PDPage pageNew = new PDPage();
                        document.addPage(pageNew);
                        contentStream = new PDPageContentStream(document, pageNew);
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);
                        contentStream.newLineAtOffset(startX, startY);
                    }
                    contentStream.showText(listy.get(i));
                    contentStream.newLineAtOffset(0, -leading);
                }

                contentStream.endText();

                // Make sure that the content stream is closed:
                contentStream.close();

                // Save the results and ensure that the document is properly closed:
                document.save("Unofficial Transcript.pdf");
                document.close();
            } else {
                Alert alert = new Alert(ERROR, "Cannot export an empty Transcript");
                alert.showAndWait();
            }
        } catch (IOException exception) {
            Alert alert = new Alert(ERROR, "There was an error while writing the pdf");
            alert.showAndWait();
        }
    }
}
