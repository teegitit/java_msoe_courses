/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough
 * Created: 03/18/2021
 */

package group3;

import java.util.ArrayList;
import java.util.List;

public class Prerequisite {
    private PrerequisiteType type;

    private List<Prerequisite> all = new ArrayList<>();
    private List<Prerequisite> one = new ArrayList<>();

    private Course course;

    /**
     * Parses prerequisite course information into readable text.
     * @param prerequisiteString
     */
    public Prerequisite(String prerequisiteString) {
        if (prerequisiteString.contains(" ")) {
            String[] parts = prerequisiteString.split(" ");
            for (String p : parts) {
                all.add(new Prerequisite(p));
            }
        } else {
            if (prerequisiteString.contains("|")) {
                String[] parts = prerequisiteString.split("\\|");
                for (String p : parts) {
                    one.add(new Prerequisite(p));
                }
            } else {
                type = determineType(prerequisiteString);
                if (type == PrerequisiteType.Course) {
                    try {
                        course = Curriculum.getCourse(prerequisiteString);
                    } catch (IllegalArgumentException ex) {
                        course = new Course(prerequisiteString, "UNKNOWN COURSE", -1, "");
                    }
                }
            }
        }
    }

    public boolean isCourse() {
        return course != null;
    }

    public Course getCourse() {
        if (!isCourse()) throw new IllegalArgumentException();
        return course;
    }

    public List<Prerequisite> getPrerequisites() {
        if (isCourse()) throw new IllegalArgumentException();
        if (all.size() > 0) {
            return new ArrayList<>(all);
        } else if (one.size() > 0) {
            return new ArrayList<>(one);
        } else {
            return new ArrayList<Prerequisite>();
        }
    }

    /**
     * Does the student have prerequisites satisfied to proceed with classes?
     * @return true or false, does the student fulfill all prerequisites courses.
     */
    public boolean isSatisfied() {
        boolean satisfied;
        if (all.size() > 0) {
            satisfied = true;
            for (Prerequisite prereq : all) {
                if (!(prereq.isSatisfied() && satisfied)) {
                    satisfied = false;
                }
            }
            return satisfied;
        } else if (one.size() > 0) {
            satisfied = false;
            for (Prerequisite prereq : one) {
                if (!satisfied && prereq.isSatisfied()) {
                    satisfied = true;
                }
            }
            return satisfied;
        } else {
            if (type != PrerequisiteType.Course) return true;
            return Transcript.getCourseGradeStatus(course.getCourseCode());
        }
    }

    @Override
    public String toString() {
        String string;
        if (all.size() > 0) {
            string = "(" + all.get(0).toString() + ")";
            for (int i = 1; i < all.size(); i++) {
                string += " AND (" + all.get(i).toString() + ")";
            }
            return string;
        } else if (one.size() > 0) {
            string = "(" + one.get(0).toString() + ")";
            for (int i = 1; i < one.size(); i++) {
                string += " OR (" + one.get(i).toString() + ")";
            }
            return string;
        } else {
            if (course != null) {
                return course.getCourseCode();
            } else {
                return type.name();
            }
        }
    }

    /**
     * Determine what type of prerequisite course the student is taking
     * @param prerequisiteString the code used to describe the prerequisite
     * @return the enum used to classify the prerequisite info
     */
    private static PrerequisiteType determineType(String prerequisiteString) {
        switch (prerequisiteString.toUpperCase()) {
            case "IC":
                return PrerequisiteType.InstructorConsent;
            case "PD":
                return PrerequisiteType.ProgramDirectorConsent;
            case "DC":
                return PrerequisiteType.DepartmentChairConsent;
            case "SO":
                return PrerequisiteType.SophomoreStanding;
            case "JR":
                return PrerequisiteType.JuniorStanding;
            case "SR":
                return PrerequisiteType.SeniorStanding;
            case "CE":
                return PrerequisiteType.ComputerEngineeringMajorsOnly;
            case "UX":
                return PrerequisiteType.UserExperienceMajorsOnly;
            default:
                return PrerequisiteType.Course;
        }
    }

    public enum PrerequisiteType {
        Course,
        InstructorConsent,
        ProgramDirectorConsent,
        DepartmentChairConsent,
        SophomoreStanding,
        JuniorStanding,
        SeniorStanding,
        ComputerEngineeringMajorsOnly,
        UserExperienceMajorsOnly
    }

}
