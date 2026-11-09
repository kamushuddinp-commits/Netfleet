package com.learnhub.service;

import com.learnhub.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * LearnHub Data Management Service.
 *
 * Demonstrates:
 * 1. Data management using Java collections.
 * 2. Dynamic Array Storage: Using java.util.ArrayList to manage all entities.
 * 3. Object Reference Passing: Connecting Student and Course instances via direct reference
 *    linkage inside Enrollment instances.
 * 4. Many-to-Many (M:N) Relationship: Dynamically managing bidirectional relationships.
 */
@Service
public class LearnHubService {

    // Dynamic array storage for portal data
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Student> students = new ArrayList<>();
    private final ArrayList<Course> courses = new ArrayList<>();
    private final ArrayList<Enrollment> enrollments = new ArrayList<>();

    // Counters for unique ID generation
    private final AtomicInteger studentIdSeq = new AtomicInteger(100);
    private final AtomicInteger courseIdSeq = new AtomicInteger(200);
    private final AtomicInteger enrollmentIdSeq = new AtomicInteger(1000);

    @PostConstruct
    public void init() {
        seedInitialData();
    }

    /**
     * Seeds initial portal data to showcase the Many-to-Many (M:N) relationships immediately.
     */
    public synchronized void seedInitialData() {
        if (!users.isEmpty()) {
            return;
        }

        // 1. Create Administrator
        Admin admin = new Admin(
                "ADM-001",
                "Portal Administrator",
                "admin@learnhub.com",
                "admin123",
                "EMP-9001",
                "Academic Affairs"
        );
        users.add(admin);

        // 2. Create Sample Students (Dynamic ArrayList storage)
        Student alice = new Student("STU-101", "Alice Johnson", "alice@example.com", "pass123", "STU-2026-001", "Computer Science");
        Student bob = new Student("STU-102", "Bob Smith", "bob@example.com", "pass123", "STU-2026-002", "Software Engineering");
        Student charlie = new Student("STU-103", "Charlie Davis", "charlie@example.com", "pass123", "STU-2026-003", "Information Technology");
        Student diana = new Student("STU-104", "Diana Prince", "diana@example.com", "pass123", "STU-2026-004", "Data Science");

        addStudent(alice);
        addStudent(bob);
        addStudent(charlie);
        addStudent(diana);

        // 3. Create Online Courses
        Course javaCourse = new Course(
                "CRS-101",
                "CS101",
                "Java Programming & OOP Architecture",
                "Software Engineering",
                "Master object-oriented concepts, class modeling, dynamic memory arrays, and reference passing in modern Java.",
                "Dr. James Gosling",
                4,
                25,
                "bi-filetype-java"
        );

        Course pythonCourse = new Course(
                "CRS-102",
                "CS102",
                "Python for Data Science & AI",
                "Data Science",
                "Explore Python data structures, NumPy, Pandas, and machine learning pipelines for predictive analytics.",
                "Prof. Guido Rossum",
                3,
                30,
                "bi-cpu"
        );

        Course webCourse = new Course(
                "CRS-103",
                "CS103",
                "Full-Stack Modern Web Development",
                "Web Engineering",
                "Build responsive end-to-end web applications with modern HTML5, CSS3, JavaScript, REST APIs, and backend architectures.",
                "Sarah Connor",
                4,
                35,
                "bi-globe2"
        );

        Course dbCourse = new Course(
                "CRS-104",
                "CS104",
                "Relational & NoSQL Database Systems",
                "Database Systems",
                "Design relational schemas, optimize SQL queries, and implement ER mapping, transactions, and indexing patterns.",
                "Dr. Edgar Codd",
                3,
                20,
                "bi-database"
        );

        Course cloudCourse = new Course(
                "CRS-105",
                "CS105",
                "Cloud Architecture & DevOps Pipelines",
                "Cloud Computing",
                "Deploy microservices, containerize applications with Docker, and configure automated CI/CD pipelines.",
                "Alex Rivera",
                3,
                25,
                "bi-cloud-check"
        );

        courses.add(javaCourse);
        courses.add(pythonCourse);
        courses.add(webCourse);
        courses.add(dbCourse);
        courses.add(cloudCourse);

        // 4. Establish Many-to-Many ER Enrollments via Object Reference Passing:
        // Demonstrating:
        // - Student Alice enrolled in 3 courses: Java, Python, Web Development
        // - Course Java has multiple students: Alice, Bob, Diana
        // - Course Python has multiple students: Alice, Charlie
        // - Course Web has multiple students: Alice, Charlie
        enrollStudentInCourse(alice, javaCourse, LocalDate.now().minusDays(15), "A+");
        enrollStudentInCourse(alice, pythonCourse, LocalDate.now().minusDays(10), "A");
        enrollStudentInCourse(alice, webCourse, LocalDate.now().minusDays(5), "In Progress");

        enrollStudentInCourse(bob, javaCourse, LocalDate.now().minusDays(12), "B+");
        enrollStudentInCourse(bob, dbCourse, LocalDate.now().minusDays(8), "A");

        enrollStudentInCourse(charlie, pythonCourse, LocalDate.now().minusDays(7), "A");
        enrollStudentInCourse(charlie, webCourse, LocalDate.now().minusDays(6), "In Progress");
        enrollStudentInCourse(charlie, cloudCourse, LocalDate.now().minusDays(2), "In Progress");

        enrollStudentInCourse(diana, javaCourse, LocalDate.now().minusDays(3), "In Progress");
        enrollStudentInCourse(diana, cloudCourse, LocalDate.now().minusDays(1), "In Progress");
    }

    private void addStudent(Student student) {
        students.add(student);
        users.add(student);
    }

    // =========================================================================
    // MANY-TO-MANY (M:N) CORE OPERATIONS & OBJECT REFERENCE PASSING
    // =========================================================================

    /**
     * Connects a Student and a Course using Object Reference Passing.
     * Creates an Enrollment associative object and stores its reference in:
     * 1. Student's dynamic ArrayList
     * 2. Course's dynamic ArrayList
     * 3. Service master dynamic ArrayList
     *
     * @param student The Student object reference
     * @param course  The Course object reference
     * @return The newly linked Enrollment object
     */
    public synchronized Enrollment enrollStudentInCourse(Student student, Course course, LocalDate date, String grade) {
        if (student == null || course == null) {
            throw new IllegalArgumentException("Student and Course references must not be null.");
        }

        // Check if student is already enrolled in this course
        if (student.isEnrolledIn(course)) {
            throw new IllegalStateException("Student " + student.getFullName() + " is already enrolled in " + course.getTitle());
        }

        // Check capacity constraint
        if (course.isFull()) {
            throw new IllegalStateException("Course " + course.getTitle() + " has reached maximum capacity of " + course.getMaxCapacity());
        }

        String enrollmentId = "ENR-" + enrollmentIdSeq.incrementAndGet();

        // OBJECT REFERENCE PASSING:
        // Passing live object references into the Enrollment constructor
        Enrollment enrollment = new Enrollment(enrollmentId, student, course, date);
        if (grade != null) {
            enrollment.setGrade(grade);
        }

        // BIDIRECTIONAL REFERENCE LINKAGE:
        // Add the enrollment reference to both entities' dynamic ArrayLists
        student.addEnrollment(enrollment);
        course.addEnrollment(enrollment);

        // Store in master dynamic list
        enrollments.add(enrollment);

        return enrollment;
    }

    /**
     * Convenience enrollment method using IDs.
     */
    public synchronized Enrollment enroll(String studentId, String courseId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " was not found.");
        }

        Course course = findCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " was not found.");
        }

        return enrollStudentInCourse(student, course, LocalDate.now(), "In Progress");
    }

    /**
     * Drops a student from a course, performing dynamic reference cleanup
     * across both the Student and Course ArrayLists.
     *
     * @param studentId ID of the student
     * @param courseId  ID of the course
     * @return true if successfully dropped, false otherwise
     */
    public synchronized boolean dropCourse(String studentId, String courseId) {
        Student student = findStudentById(studentId);
        Course course = findCourseById(courseId);

        if (student == null || course == null) {
            return false;
        }

        Enrollment enrollmentToDrop = null;
        for (Enrollment e : student.getEnrollments()) {
            if (e != null && e.isActive() && e.getCourse() != null &&
                    courseId.equalsIgnoreCase(e.getCourse().getCourseId())) {
                enrollmentToDrop = e;
                break;
            }
        }

        if (enrollmentToDrop != null) {
            // Update status
            enrollmentToDrop.setStatus("DROPPED");
            // Remove from student and course dynamic lists
            student.removeEnrollment(enrollmentToDrop);
            course.removeEnrollment(enrollmentToDrop);
            return true;
        }

        return false;
    }

    // =========================================================================
    // STUDENT DATA MANAGEMENT
    // =========================================================================

    public synchronized Student registerStudent(String fullName, String email, String password, String major) {
        if (findUserByEmail(email) != null) {
            throw new IllegalArgumentException("An account with email " + email + " already exists.");
        }

        String studentId = "STU-" + studentIdSeq.incrementAndGet();
        String studentNumber = "STU-2026-" + String.format("%03d", studentIdSeq.get());

        Student student = new Student(studentId, fullName, email, password, studentNumber, major);
        addStudent(student);
        return student;
    }

    public synchronized ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public synchronized Student findStudentById(String studentId) {
        if (studentId == null) return null;
        for (Student s : students) {
            if (studentId.equalsIgnoreCase(s.getId())) {
                return s;
            }
        }
        return null;
    }

    // =========================================================================
    // COURSE DATA MANAGEMENT
    // =========================================================================

    public synchronized Course createCourse(String code, String title, String category,
                                           String description, String instructor, int credits, int capacity) {
        String courseId = "CRS-" + courseIdSeq.incrementAndGet();
        String icon = determineCourseIcon(category);

        Course course = new Course(
                courseId,
                code.toUpperCase().trim(),
                title.trim(),
                category.trim(),
                description.trim(),
                instructor.trim(),
                credits > 0 ? credits : 3,
                capacity > 0 ? capacity : 30,
                icon
        );

        courses.add(course);
        return course;
    }

    private String determineCourseIcon(String category) {
        if (category == null) return "bi-journal-code";
        String lower = category.toLowerCase();
        if (lower.contains("software") || lower.contains("java") || lower.contains("prog")) {
            return "bi-filetype-java";
        } else if (lower.contains("data") || lower.contains("ai") || lower.contains("python")) {
            return "bi-cpu";
        } else if (lower.contains("web")) {
            return "bi-globe2";
        } else if (lower.contains("data") || lower.contains("sql")) {
            return "bi-database";
        } else if (lower.contains("cloud") || lower.contains("devops")) {
            return "bi-cloud-check";
        } else if (lower.contains("security") || lower.contains("cyber")) {
            return "bi-shield-lock";
        }
        return "bi-journal-code";
    }

    public synchronized ArrayList<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public synchronized Course findCourseById(String courseId) {
        if (courseId == null) return null;
        for (Course c : courses) {
            if (courseId.equalsIgnoreCase(c.getCourseId())) {
                return c;
            }
        }
        return null;
    }

    public synchronized ArrayList<Course> searchCourses(String query, String category) {
        return courses.stream()
                .filter(c -> {
                    boolean matchesQuery = true;
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.trim().toLowerCase();
                        matchesQuery = c.getTitle().toLowerCase().contains(q) ||
                                       c.getCourseCode().toLowerCase().contains(q) ||
                                       c.getInstructor().toLowerCase().contains(q) ||
                                       c.getDescription().toLowerCase().contains(q);
                    }
                    boolean matchesCategory = true;
                    if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
                        matchesCategory = category.equalsIgnoreCase(c.getCategory());
                    }
                    return matchesQuery && matchesCategory;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public synchronized List<String> getAllCategories() {
        return courses.stream()
                .map(Course::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // =========================================================================
    // USER AUTHENTICATION & ACCESS
    // =========================================================================

    public synchronized User authenticate(String email, String password) {
        if (email == null || password == null) return null;
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public synchronized User findUserByEmail(String email) {
        if (email == null) return null;
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                return u;
            }
        }
        return null;
    }

    // =========================================================================
    // ENROLLMENTS & SYSTEM METRICS
    // =========================================================================

    public synchronized ArrayList<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }

    public synchronized List<Enrollment> getRecentEnrollments(int limit) {
        return enrollments.stream()
                .sorted(Comparator.comparing(Enrollment::getEnrollmentDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public synchronized Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalStudents", students.size());
        metrics.put("totalCourses", courses.size());
        metrics.put("totalEnrollments", enrollments.size());

        long activeEnrollmentsCount = enrollments.stream().filter(Enrollment::isActive).count();
        metrics.put("activeEnrollments", activeEnrollmentsCount);

        double avgCoursesPerStudent = students.isEmpty() ? 0.0 :
                (double) activeEnrollmentsCount / students.size();
        metrics.put("avgCoursesPerStudent", String.format("%.1f", avgCoursesPerStudent));

        Course mostPopularCourse = courses.stream()
                .max(Comparator.comparingInt(Course::getEnrolledCount))
                .orElse(null);
        metrics.put("mostPopularCourse", mostPopularCourse != null ? mostPopularCourse.getTitle() : "N/A");

        return metrics;
    }
}
