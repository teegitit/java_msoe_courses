/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough
 * Created: 03/18/2021
 */

package group3;

import javafx.scene.control.Alert;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Contains the courses, curriculum, and offerings for each major and term.
 * load() should be called upon loading the program to ensure that all views
 * have access to the appropriate data.
 * @author Kenneth McDonough
 * @version 2021.03.29
 */
public class Curriculum {
    private static HashMap<String, Course> courses = new HashMap<>();
    private static List<String> fallSEOfferings = new ArrayList<>();
    private static List<String> winterSEOfferings = new ArrayList<>();
    private static List<String> springSEOfferings = new ArrayList<>();
    private static List<String> fallCSOfferings = new ArrayList<>();
    private static List<String> winterCSOfferings = new ArrayList<>();
    private static List<String> springCSOfferings = new ArrayList<>();

    private static List<Course> seCurriculum = new ArrayList<>();
    private static List<Course> csCurriculum = new ArrayList<>();

    private static List<List<String>> errors = new ArrayList<>();

    /**
     * Loads all necessary data for the class to function.
     */
    protected static void load() {
        loadCourses();
        loadPrerequisites();
        loadOfferings();
        loadCurriculum();
    }

    /**
     * Populates the available courses by reading the prerequisites.csv file
     * bundled in the program. ALWAYS CALLED FIRST
     */
    private static void loadCourses() {
        Scanner scanner = new Scanner(Curriculum.class.getResourceAsStream("prerequisites.csv"));
        scanner.nextLine(); // push out file header
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                String[] parts = line.split(",", 4);

                String code = parts[0].trim();
                int credits = Integer.parseInt(parts[1].trim());
                String prerequisites = parts[2].trim();
                String description = parts[3].trim();

                List<String> err = new ArrayList<>();
                if (!code.matches("^([A-Z]{2}\\d{3,4})(.*)") || credits <= 0 || description.equals("-")) {
                    err.add("Error in prerequisites.csv");

                    String error;
                    if (description.equals("-")) {
                        error = line.substring(0, line.length()-1);
                    } else {
                        error = line;
                    }

                    if (!code.matches("^([A-Z]{2}\\d{3,4})(.*)")) {
                        error = error + " --Course Has An Invalid Course Code";
                    }
                    if (credits <= 0) {
                        error = error + " --Course Has An Invalid Credit Value";
                    }
                    if (description.equals("-")) {
                        error = error + " --Course Has An Empty Description";
                    }

                    err.add(error);
                    errors.add(err);
                    continue;
                }
                courses.put(code, new Course(code, description, credits, prerequisites));
            } catch (NumberFormatException ex) {
                List<String> err = new ArrayList<>();
                err.add("Error in prerequisites.csv");
                err.add(line + " --Course's Credits were not a valid number");
                errors.add(err);
            }
        }
    }

    /**
     * Overwrites the loaded courses with their prerequisite information.
     * ALWAYS CALLED SECOND
     */
    private static void loadPrerequisites() {
        HashMap<String, Course> newCourses = new HashMap<>();
        for (Course oldCourse: courses.values()) {
            newCourses.put(oldCourse.getCourseCode(), new Course(oldCourse.getCourseCode(), oldCourse.getDescription(), oldCourse.getCredits(), new Prerequisite(oldCourse.getRawPrerequisites())));
        }

        courses = newCourses;
    }

    /**
     * Populates the offerings for each term by reading the offerings.csv file
     * bundled in the program. ALWAYS CALLED THIRD
     */
    private static void loadOfferings() {
        Scanner scanner = new Scanner(Curriculum.class.getResourceAsStream("offerings.csv"));
        String header = scanner.nextLine();
        int se = -1;
        int cs = -1;
        String[] headerParts = header.split(",");
        for (int i = 0; i < headerParts.length; i++) {
            if (headerParts[i].equalsIgnoreCase("SE")) se = i;
            if (headerParts[i].equalsIgnoreCase("CS")) cs = i;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] e = line.split(",", -1); // -1: include trailing values

            if (e.length != 15) {
                List<String> err = new ArrayList<>();
                err.add("Error in offerings.csv");
                err.add(line + " Is not a Valid Offering, Missing some class trimester Info");
                errors.add(err);
                continue;
            }

            if (e[se].equals("1")) fallSEOfferings.add(e[0]);
            if (e[se].equals("2")) winterSEOfferings.add(e[0]);
            if (e[se].equals("3")) springSEOfferings.add(e[0]);
            if (e[cs].equals("1")) fallCSOfferings.add(e[0]);
            if (e[cs].equals("2")) winterCSOfferings.add(e[0]);
            if (e[cs].equals("3")) springCSOfferings.add(e[0]);
        }
    }

    /**
     * Populates the curriculum for each major by reading the curriculum.csv file
     * bundled in the program. ALWAYS CALLED FOURTH
     */
    private static void loadCurriculum() {
        Scanner scanner = new Scanner(Curriculum.class.getResourceAsStream("curriculum.csv"));
        String[] header = scanner.nextLine().split(",");

        int se = -1, cs = -1;

        for (int i = 0; i < header.length; i++) {
            if (header[i].equalsIgnoreCase("SE")) se = i;
            if (header[i].equalsIgnoreCase("CS")) cs = i;
        }

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(",");

            Course csCourse;
            Course seCourse;

            try {
                csCourse = getCourse(line[cs]);
            } catch (IllegalArgumentException ex) {
                List<String> err = new ArrayList<>();
                err.add("Error in curriculum.csv");
                err.add(ex.toString());
                errors.add(err);

                csCourse = null;
            }

            try {
                seCourse = getCourse(line[se]);
            } catch (IllegalArgumentException ex) {
                seCourse = null;
            }

            if (line[cs].length() != 0 && csCourse == null) csCourse = new Course(line[cs], line[cs], 0, "");
            if (line[se].length() != 0 && seCourse == null) seCourse = new Course(line[se], line[se], 0, "");

            if (csCourse != null) csCurriculum.add(csCourse);
            if (seCourse != null) seCurriculum.add(seCourse);
        }
    }

    /**
     * @param major major to use; SE or CS
     * @param term term to use; Fall, Winter, or Spring
     * @return a list of all courses available in the given term for the given major
     */
    public static List<Course> getOfferings(Major major, Term term) {
        List<Course> offerings = new ArrayList<Course>();
        if (major == Major.SE) {
            if (term == Term.Fall) {
                for (String s : fallSEOfferings) offerings.add(getCourse(s));
            } else if (term == Term.Winter) {
                for (String s : winterSEOfferings) offerings.add(getCourse(s));
            } else if (term == Term.Spring) {
                for (String s : springSEOfferings) offerings.add(getCourse(s));
            }
        } else if (major == Major.CS) {
            if (term == Term.Fall) {
                for (String s : fallCSOfferings) offerings.add(getCourse(s));
            } else if (term == Term.Winter) {
                for (String s : winterCSOfferings) offerings.add(getCourse(s));
            } else if (term == Term.Spring) {
                for (String s : springCSOfferings) offerings.add(getCourse(s));
            }
        }
        return offerings;
    }

    /**
     * Get a course by its course code
     * @param code the course code (e.x. SE2800)
     * @return the course
     */
    public static Course getCourse(String code) {
        if (courses.containsKey(code)) {
            return courses.get(code);
        } else if (code.matches("([A-Z]{2}\\d{3,4}\\w?)")) {
            return new Course(code, "unknown elective", 0, "");
        } else if (code.length() > 6) {
            // TODO: Handle international student transcripts differently
            code = code.substring(0,6);
            return courses.get(code);
        } else {
            throw new IllegalArgumentException("Course " + code + " not found.");
        }
    }

    /**
     * Gets the curriculum for a given major
     * @param major major to use; SE or CS
     * @return a list of all courses a student in the given major needs to graduate, or null if the major is invalid
     */
    public static List<Course> getCurriculum(Major major) {
        if (major == Major.SE) return seCurriculum;
        if (major == Major.CS) return csCurriculum;
        throw new IllegalArgumentException("Curriculum for major " + major + " not found.");
    }

    /**
     * Given a string representation of a major, determines what major it references
     * @param string string representation; SE, Software, Software Engineering, CS, Computer Science, etc.
     * @return the the determined major, or null if the major cannot be determined
     */
    public static Major stringToMajor(String string) {
        if (string.toLowerCase().contains("software") || string.equalsIgnoreCase("SE")) {
            return Major.SE;
        } else if (string.toLowerCase().contains("science") || string.equalsIgnoreCase("CS")) {
            return Major.CS;
        }
        throw new IllegalArgumentException("Major " + string + " not found.");
    }

    /**
     * Given a string representation of a term, determines what term it references
     * @param string string representation; fall, winter, spring
     * @return the the determined term, or null if the term cannot be determined
     */
    public static Term stringToTerm(String string) {
        if (string.toLowerCase().contains("fall")) {
            return Term.Fall;
        } else if (string.toLowerCase().contains("winter")) {
            return Term.Winter;
        } else if (string.toLowerCase().contains("spring")) {
            return Term.Spring;
        }
        throw new IllegalArgumentException("Term " + string + " not found.");
    }

    public static List<List<String>> getErrors() {
        return errors;
    }

    /**
     * Majors
     */
    public enum Major {
        SE,
        CS
    }

    /**
     * Terms
     */
    public enum Term {
        Fall,
        Winter,
        Spring
    }
}
