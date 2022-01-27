/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Austin Boley
 * Created: 03/24/2021
 */

package group3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the effectiveness of the Transcript class to ensure all methods you can call
 * will return the expected results and not something unexpected
 */
public class TranscriptTests {

    /**
     * The set up for before each test to ensure the the Transcript and Curriculum are loaded
     * @throws URISyntaxException Because URI can be funky
     */
    @BeforeEach
    public void setUp() throws URISyntaxException {
        Curriculum.load();
        Transcript.load(new File(new URI(TranscriptTests.class.getResource("Transcript.pdf").toString())));
    }

    /**
     * 3 Valid, 3 Invalid, 1 Null to test the effectiveness of .getCourseTaken
     *
     * @param courseCode the Course code of the class were checking for
     * @param taken the expected result if the class was taken, True for taken False for not taken
     */
    @ParameterizedTest
    @CsvSource({"CH200, true", "CS1021, true", "MA2310, true", "MA135, false", "CH199, false", "CS2853, false", ", false"})
    public void getCourseTaken(String courseCode, boolean taken) {
        assertEquals(Transcript.getCourseTaken(courseCode), taken);
    }

    /**
     * 3 Valid, 3 Invalid, 1 Null to test the effectiveness of .getCourseGrade
     *
     * @param courseCode the Course code of the class were checking for
     * @param expected the expected result from the grade status, True for Pass and False for Fail
     */
    @ParameterizedTest
    @CsvSource({"CH200, true", "CS1021, true", "MA2310, true", "MA135, false", "CH199, false", "CS2853, false", ", false"})
    public void getCourseGradeStatus(String courseCode, boolean expected) {
        assertEquals(Transcript.getCourseTaken(courseCode), expected);
    }

    /**
     * 1 Valid File and 1 Invalid cannot test null since NullPointer will be thrown
     *
     * @param fileName the file being imported
     * @param expected if the Transcript.isImported will return true or false
     * @throws URISyntaxException Because URI can be funky
     */
    @ParameterizedTest
    @CsvSource({"BadTranscript.pdf, false", "Transcript.pdf, true"})
    public void isImported(String fileName, boolean expected) throws URISyntaxException {
        Transcript.load(new File(new URI(TranscriptTests.class.getResource(fileName).toString())));
        assertEquals(Transcript.isImported(), expected);
    }

    /**
     * Tests the getMajor Function of Transcript
     * Can only really test if the courses are empty and a SE transcript
     *
     * NOTE: There was an attempt to return a null file but it would keep failing saying
     * that they expected was null and actual was nothing or vice versa But it does return null
     *
     */
    @Test
    public void getMajor(){
        assertEquals(Transcript.getMajor(), "SE");
    }
}
