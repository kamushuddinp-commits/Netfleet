package com.learnhub;

import com.learnhub.model.Course;
import com.learnhub.model.Enrollment;
import com.learnhub.model.Student;
import com.learnhub.service.LearnHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests specifically demonstrating:
 * 1. Classes and Objects.
 * 2. Dynamic Array Storage using ArrayList.
 * 3. Object Reference Passing.
 * 4. Many-to-Many (M:N) ER Relationships.
 * 5. Data Management using Java.
 */
public class ManyToManyRelationshipTest {

    private LearnHubService service;

    @BeforeEach
    public void setUp() {
        service = new LearnHubService();
        service.seedInitialData();
    }

    @Test
    @DisplayName("Demonstrate Dynamic Array Storage using ArrayList")
    public void testDynamicArrayStorage() {
        Student student = new Student("TEST-01", "John Doe", "john@test.com", "pass", "STU-999", "CS");
        Course course1 = new Course("C1", "CS101", "Course 1", "CS", "Desc", "Prof A", 3, 30, "bi-code");
        Course course2 = new Course("C2", "CS102", "Course 2", "CS", "Desc", "Prof B", 3, 30, "bi-code");
        Course course3 = new Course("C3", "CS103", "Course 3", "CS", "Desc", "Prof C", 3, 30, "bi-code");

        // Initially 0 enrollments
        assertEquals(0, student.getEnrollments().size(), "Initial ArrayList size should be 0");

        // Dynamically add enrollments (ArrayList expands without fixed size limits)
        Enrollment e1 = new Enrollment("E1", student, course1, LocalDate.now());
        Enrollment e2 = new Enrollment("E2", student, course2, LocalDate.now());
        Enrollment e3 = new Enrollment("E3", student, course3, LocalDate.now());

        student.addEnrollment(e1);
        student.addEnrollment(e2);
        student.addEnrollment(e3);

        assertEquals(3, student.getEnrollments().size(), "ArrayList dynamically resized to 3");
        assertEquals(3, student.getEnrolledCourses().size());

        // Dynamically remove an enrollment (ArrayList shrinks automatically)
        student.removeEnrollment(e2);
        assertEquals(2, student.getEnrollments().size(), "ArrayList dynamically shrank to 2");
        assertFalse(student.isEnrolledIn("C2"), "Course 2 should no longer be enrolled");
        assertTrue(student.isEnrolledIn("C1"), "Course 1 should still be enrolled");
        assertTrue(student.isEnrolledIn("C3"), "Course 3 should still be enrolled");
    }

    @Test
    @DisplayName("Demonstrate Object Reference Passing between Student, Course, and Enrollment")
    public void testObjectReferencePassing() {
        Student alice = service.findStudentById("STU-101");
        Course javaCourse = service.findCourseById("CRS-101");

        assertNotNull(alice);
        assertNotNull(javaCourse);

        // Find Alice's enrollment in Java
        Enrollment enrollment = alice.getEnrollmentForCourse("CRS-101");
        assertNotNull(enrollment);

        // Verify that enrollment holds the exact reference to the course object
        assertSame(javaCourse, enrollment.getCourse(),
                "Reference equality check: enrollment.getCourse() must point to the identical Course object in heap");

        // Verify that enrollment holds the exact reference to Alice
        assertSame(alice, enrollment.getStudent(),
                "Reference equality check: enrollment.getStudent() must point to the identical Student object in heap");

        // Demonstrate mutation through reference passing:
        // Updating the course instructor directly on javaCourse
        String updatedInstructor = "Dr. James Gosling & Team";
        javaCourse.setInstructor(updatedInstructor);

        // Accessing through Alice's enrolled courses reflects the update immediately without database refresh
        Course courseAccessedFromStudent = alice.getEnrolledCourses().stream()
                .filter(c -> "CRS-101".equals(c.getCourseId()))
                .findFirst()
                .orElse(null);

        assertNotNull(courseAccessedFromStudent);
        assertEquals(updatedInstructor, courseAccessedFromStudent.getInstructor(),
                "Object reference ensures mutation on Course is immediately visible through Student's reference!");
    }

    @Test
    @DisplayName("Demonstrate Many-to-Many (M:N) Relationship: 1 Student in Multiple Courses, 1 Course with Multiple Students")
    public void testManyToManyRelationship() {
        Student alice = service.findStudentById("STU-101"); // Alice
        assertNotNull(alice);

        // 1. One Student -> Multiple Courses
        ArrayList<Course> aliceCourses = alice.getEnrolledCourses();
        assertTrue(aliceCourses.size() >= 3, "Alice should be enrolled in at least 3 courses (Java, Python, Web Dev)");

        boolean hasJava = aliceCourses.stream().anyMatch(c -> c.getTitle().contains("Java"));
        boolean hasPython = aliceCourses.stream().anyMatch(c -> c.getTitle().contains("Python"));
        boolean hasWeb = aliceCourses.stream().anyMatch(c -> c.getTitle().contains("Web"));

        assertTrue(hasJava, "Alice should be enrolled in Java");
        assertTrue(hasPython, "Alice should be enrolled in Python");
        assertTrue(hasWeb, "Alice should be enrolled in Web Development");

        // 2. One Course -> Multiple Students
        Course javaCourse = service.findCourseById("CRS-101");
        assertNotNull(javaCourse);

        ArrayList<Student> javaStudents = javaCourse.getEnrolledStudents();
        assertTrue(javaStudents.size() >= 2, "Java course should have multiple students enrolled");

        boolean aliceInJava = javaStudents.stream().anyMatch(s -> s.getId().equals(alice.getId()));
        boolean bobInJava = javaStudents.stream().anyMatch(s -> s.getId().equals("STU-102"));

        assertTrue(aliceInJava, "Java course roster must contain Alice");
        assertTrue(bobInJava, "Java course roster must contain Bob");
    }

    @Test
    @DisplayName("Prevent Duplicate Enrollments for the Same Student in the Same Course")
    public void testDuplicateEnrollmentPrevention() {
        Student alice = service.findStudentById("STU-101");
        Course javaCourse = service.findCourseById("CRS-101");

        // Alice is already enrolled in Java from seed data
        assertTrue(alice.isEnrolledIn(javaCourse));

        // Attempting to enroll again should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            service.enrollStudentInCourse(alice, javaCourse, LocalDate.now(), "In Progress");
        }, "Should throw exception when attempting to enroll twice");
    }

    @Test
    @DisplayName("Demonstrate Unenrollment / Drop Course Reference Cleanup")
    public void testDropCourseReferenceCleanup() {
        Student bob = service.findStudentById("STU-102");
        Course dbCourse = service.findCourseById("CRS-104");

        assertTrue(bob.isEnrolledIn(dbCourse));
        assertTrue(dbCourse.hasStudent(bob));

        int initialBobCount = bob.getActiveEnrollmentCount();
        int initialDbStudents = dbCourse.getEnrolledCount();

        // Drop Bob from Database course
        boolean dropped = service.dropCourse(bob.getId(), dbCourse.getCourseId());
        assertTrue(dropped, "Course drop should be successful");

        // Verify Bob's enrollment count decreased
        assertEquals(initialBobCount - 1, bob.getActiveEnrollmentCount());
        assertFalse(bob.isEnrolledIn(dbCourse));

        // Verify Course's enrolled student list decreased and no longer contains Bob
        assertEquals(initialDbStudents - 1, dbCourse.getEnrolledCount());
        assertFalse(dbCourse.hasStudent(bob));
    }

    @Test
    @DisplayName("Demonstrate Course Capacity Limit Enforcement")
    public void testCapacityConstraint() {
        // Create a small course with capacity of 1
        Course microCourse = service.createCourse("CS999", "Micro Course", "Test", "Test", "Prof X", 1, 1);
        Student s1 = service.findStudentById("STU-101");
        Student s2 = service.findStudentById("STU-102");

        // First student enrolls -> capacity reached
        service.enrollStudentInCourse(s1, microCourse, LocalDate.now(), "In Progress");
        assertTrue(microCourse.isFull());
        assertEquals(0, microCourse.getAvailableSeats());

        // Second student enrollment should fail due to capacity
        assertThrows(IllegalStateException.class, () -> {
            service.enrollStudentInCourse(s2, microCourse, LocalDate.now(), "In Progress");
        });
    }
}
