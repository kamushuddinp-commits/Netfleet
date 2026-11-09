package com.learnhub.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Associative Entity modeling the Many-to-Many (M:N) relationship
 * between Student and Course.
 *
 * Demonstrates:
 * 1. Object Reference Passing: Holds direct references to Student and Course objects in memory.
 * 2. Relationship attributes: enrollmentId, enrollmentDate, status, grade.
 */
public class Enrollment {
    private String enrollmentId;
    
    // Direct object references (Reference Passing)
    private Student student;
    private Course course;
    
    private LocalDate enrollmentDate;
    private String status; // ACTIVE, COMPLETED, DROPPED
    private String grade;  // e.g. "A+", "A", "In Progress"
    private String notes;

    public Enrollment() {
        this.enrollmentDate = LocalDate.now();
        this.status = "ACTIVE";
        this.grade = "In Progress";
    }

    /**
     * Constructor accepting object references.
     * 
     * @param enrollmentId Unique identifier for this enrollment instance
     * @param student Reference to the enrolled Student object in heap memory
     * @param course Reference to the target Course object in heap memory
     * @param enrollmentDate Date of enrollment
     */
    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate) {
        this.enrollmentId = enrollmentId;
        // OBJECT REFERENCE PASSING:
        // We do not copy student or course fields; we reference the live instances directly.
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate != null ? enrollmentDate : LocalDate.now();
        this.status = "ACTIVE";
        this.grade = "In Progress";
        this.notes = "Enrolled via LearnHub Online Portal";
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getFormattedDate() {
        if (enrollmentDate == null) return "N/A";
        return enrollmentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId='" + enrollmentId + '\'' +
                ", student=" + (student != null ? student.getFullName() : "null") +
                ", course=" + (course != null ? course.getTitle() : "null") +
                ", date=" + enrollmentDate +
                ", status='" + status + '\'' +
                '}';
    }
}
