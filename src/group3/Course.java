/*
 * Course: SE 2800 - 051
 * Group 3: Thy Le, Kenneth McDonough, Austin Boley, Luke Miller
 * Spring 2021
 * Author: Kenneth McDonough
 * Created: 03/18/2021
 */

package group3;

/**
 * Object for Classes. Store relevant information about a specific class in object, including names
 * descriptions, prerequisites, etc.
 */
public class Course {
    private String course, description, rawPrerequisites;
    private int credits;
    private Prerequisite prerequisite;

    public Course(String course, String description, int credits, Prerequisite prerequisite) {
        this.course = course;
        this.description = description;
        this.credits = credits;
        this.prerequisite = prerequisite;
    }

    public Course(String course, String description, int credits, String prerequisites) {
        this.course = course;
        this.description = description;
        this.credits = credits;
        this.rawPrerequisites = prerequisites;
    }

    public String getCourseCode() {
        return this.course;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCredits() {
        return credits;
    }

    public Prerequisite getPrerequisites() {
        return prerequisite;
    }

    public String getRawPrerequisites() {
        return this.rawPrerequisites;
    }


    @Override
    public String toString() {
        return "[" + course +"] " + description + " (" + credits + " credits)\n" + prerequisite;
    }
}
