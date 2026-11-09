package com.learnhub.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student entity representing an enrolled learner in LearnHub.
 *
 * Demonstrates:
 * 1. Classes and Objects: Extends User, encapsulates student state and behavior.
 * 2. Dynamic Array Storage: Uses java.util.ArrayList to dynamically store and manage enrollments.
 * 3. Reference Passing: Interacts directly with Enrollment and Course references.
 */
public class Student extends User {
    private String major;
    private String studentNumber; // e.g., "STU-2026-001"

    // Dynamic array storage for Many-to-Many ER relationship:
    // A student can enroll in multiple courses without a fixed array bound.
    private final ArrayList<Enrollment> enrollments = new ArrayList<>();

    public Student() {
        super();
        setRole(Role.ROLE_STUDENT);
    }

    public Student(String id, String fullName, String email, String password, String studentNumber, String major) {
        super(id, fullName, email, password, Role.ROLE_STUDENT);
        this.studentNumber = studentNumber;
        this.major = major;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * Returns the dynamic list of enrollments for this student.
     */
    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }

    /**
     * Adds an enrollment reference to this student's dynamic ArrayList.
     * 
     * @param enrollment The Enrollment reference linking this student to a course
     */
    public void addEnrollment(Enrollment enrollment) {
        if (enrollment != null && !enrollments.contains(enrollment)) {
            enrollments.add(enrollment);
        }
    }

    /**
     * Removes an enrollment reference from this student's dynamic ArrayList.
     * 
     * @param enrollment The Enrollment reference to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeEnrollment(Enrollment enrollment) {
        return enrollments.remove(enrollment);
    }

    /**
     * Traverses the dynamic ArrayList of enrollments to retrieve all courses
     * currently enrolled by this student.
     * Demonstrates object reference traversal across the Many-to-Many join entity.
     * 
     * @return Dynamic ArrayList of Course object references
     */
    public ArrayList<Course> getEnrolledCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.isActive() && enrollment.getCourse() != null) {
                courses.add(enrollment.getCourse());
            }
        }
        return courses;
    }

    /**
     * Checks if this student is already actively enrolled in the specified course.
     * 
     * @param course The Course reference to check
     * @return true if already enrolled, false otherwise
     */
    public boolean isEnrolledIn(Course course) {
        if (course == null) return false;
        return isEnrolledIn(course.getCourseId());
    }

    /**
     * Checks if this student is already actively enrolled in a course with courseId.
     * 
     * @param courseId The course identifier to check
     * @return true if already enrolled, false otherwise
     */
    public boolean isEnrolledIn(String courseId) {
        if (courseId == null) return false;
        for (Enrollment e : enrollments) {
            if (e != null && e.isActive() && e.getCourse() != null && courseId.equalsIgnoreCase(e.getCourse().getCourseId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the specific active enrollment for a given course ID.
     */
    public Enrollment getEnrollmentForCourse(String courseId) {
        if (courseId == null) return null;
        for (Enrollment e : enrollments) {
            if (e != null && e.isActive() && e.getCourse() != null && courseId.equalsIgnoreCase(e.getCourse().getCourseId())) {
                return e;
            }
        }
        return null;
    }

    /**
     * Returns the total number of active course enrollments for this student.
     */
    public int getActiveEnrollmentCount() {
        int count = 0;
        for (Enrollment e : enrollments) {
            if (e != null && e.isActive()) {
                count++;
            }
        }
        return count;
    }
}
