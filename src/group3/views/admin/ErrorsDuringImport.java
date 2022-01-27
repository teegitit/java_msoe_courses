/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Austin Boley
 * Created: 05/05/2021
 */
package group3.views.admin;

import group3.Curriculum;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class ErrorsDuringImport {
    @FXML
    ListView<String> pane;

    private List<List<String>> errors;

    @FXML
    public void initialize() {
        pane.getItems().clear();

        errors = Curriculum.getErrors();

        pane.getItems().add("All errors discovered during importation will be placed here");

        String loc = "";
        for (List<String> listy : errors) {
            String location = listy.get(0);

            if (!location.equals(loc)) {
                loc = location;
                pane.getItems().add("");
                pane.getItems().add(loc);
            }

            pane.getItems().add(listy.get(1));
        }
    }

}
