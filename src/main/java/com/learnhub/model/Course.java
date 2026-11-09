package com.learnhub.model;

import java.util.ArrayList;

/**
 * Course entity representing an online learning course in LearnHub.
 *
 * Demonstrates:
 * 1. Classes and Objects: Models course properties, capacity constraints, and lifecycle.
 * 2. Dynamic Array Storage: Uses java.util.ArrayList to dynamically track all enrolled students.
 * 3. Reference Passing: Stores direct references to associative Enrollment entities.
 * 4. Many-to-Many Relationship: Allows multiple students to be connected to this single course instance.
 */
public class Course {
    private String courseId;
    private String courseCode;
    private String title;
    private String category;
    private String description;
    private String instructor;
    private int creditHours;
    private int maxCapacity;
    private String icon; // Bootstrap icon identifier (e.g., "bi-code-slash")

    // Dynamic array storage for Many-to-Many ER relationship:
    // Holds references to all enrollments linked to this course.
    private final ArrayList<Enrollment> enrollments = new ArrayList<>();

    public Course() {
        this.maxCapacity = 30;
        this.creditHours = 3;
        this.icon = "bi-journal-code";
    }

    public Course(String courseId, String courseCode, String title, String category,
                  String description, String instructor, int creditHours, int maxCapacity, String icon) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.title = title;
        this.category = category;
        this.description = description;
        this.instructor = instructor;
        this.creditHours = creditHours;
        this.maxCapacity = maxCapacity;
        this.icon = icon != null ? icon : "bi-journal-code";
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the dynamic list of enrollments for this course.
     */
    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }

    /**
     * Adds an enrollment reference to this course's dynamic ArrayList.
     * 
     * @param enrollment The Enrollment reference linking a student to this course
     */
    public void addEnrollment(Enrollment enrollment) {
        if (enrollment != null && !enrollments.contains(enrollment)) {
            enrollments.add(enrollment);
        }
    }

    /**
     * Removes an enrollment reference from this course's dynamic ArrayList.
     * 
     * @param enrollment The Enrollment reference to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeEnrollment(Enrollment enrollment) {
        return enrollments.remove(enrollment);
    }

    /**
     * Traverses the dynamic ArrayList of enrollments to retrieve all students
     * currently enrolled in this course.
     * Demonstrates object reference traversal: Course -> Enrollment -> Student.
     * 
     * @return Dynamic ArrayList of Student object references
     */
    public ArrayList<Student> getEnrolledStudents() {
        ArrayList<Student> students = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.isActive() && enrollment.getStudent() != null) {
                students.add(enrollment.getStudent());
            }
        }
        return students;
    }

    /**
     * Returns the number of actively enrolled students in this course.
     */
    public int getEnrolledCount() {
        int count = 0;
        for (Enrollment e : enrollments) {
            if (e != null && e.isActive()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the remaining seats available in this course.
     */
    public int getAvailableSeats() {
        int remaining = maxCapacity - getEnrolledCount();
        return Math.max(0, remaining);
    }

    /**
     * Checks if this course has reached its maximum enrollment capacity.
     */
    public boolean isFull() {
        return getEnrolledCount() >= maxCapacity;
    }

    /**
     * Checks if a student is already enrolled in this course.
     */
    public boolean hasStudent(Student student) {
        if (student == null) return false;
        return hasStudent(student.getId());
    }

    public boolean hasStudent(String studentId) {
        if (studentId == null) return false;
        for (Enrollment e : enrollments) {
            if (e != null && e.isActive() && e.getStudent() != null && studentId.equalsIgnoreCase(e.getStudent().getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", code='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", instructor='" + instructor + '\'' +
                ", enrolled=" + getEnrolledCount() + "/" + maxCapacity +
                '}';
    }
}
