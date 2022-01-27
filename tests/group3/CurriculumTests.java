package group3;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurriculumTests {

    @BeforeAll
    public static void initialize() {
        Curriculum.load();
    }

    @Test
    public void getCurriculum_invalidMajor_throwsException() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            Curriculum.stringToMajor("OOGA BOOGA");
        });
    }

    @Test
    public void getCurriculum_softwareEngineering_isRightSize() {
        // Arrange
        Curriculum.Major major = Curriculum.Major.SE;

        // Act
        List<Course> curriculum = Curriculum.getCurriculum(major);

        // Assert
        assertEquals(curriculum.size(), 56);
    }

    @Test
    public void getCurriculum_computerScience_isRightSize() {
        // Arrange
        Curriculum.Major major = Curriculum.Major.CS;

        // Act
        List<Course> curriculum = Curriculum.getCurriculum(major);

        // Assert
        assertEquals(curriculum.size(), 55);
    }

    @Test
    public void getCourse_physics3_hasFullName() {
        // Act
        Course physics3 = Curriculum.getCourse("PH2031");

        // Assert
        assertEquals(physics3.getDescription(), "Waves, Optics, Thermodynamics, and Quantum Physics");
    }
}
