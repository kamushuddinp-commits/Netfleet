package com.learnhub.controller;

import com.learnhub.model.Course;
import com.learnhub.model.Enrollment;
import com.learnhub.model.Student;
import com.learnhub.service.LearnHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * REST API Controller providing JSON endpoints.
 * Uses clean DTO maps to avoid cyclic JSON serialization from bidirectional Java references.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final LearnHubService service;

    @Autowired
    public ApiController(LearnHubService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getSystemMetrics());
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getCourses() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : service.getAllCourses()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("courseId", c.getCourseId());
            map.put("courseCode", c.getCourseCode());
            map.put("title", c.getTitle());
            map.put("category", c.getCategory());
            map.put("instructor", c.getInstructor());
            map.put("creditHours", c.getCreditHours());
            map.put("maxCapacity", c.getMaxCapacity());
            map.put("enrolledCount", c.getEnrolledCount());
            map.put("availableSeats", c.getAvailableSeats());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable("id") String id) {
        Course c = service.findCourseById(id);
        if (c == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("courseId", c.getCourseId());
        map.put("courseCode", c.getCourseCode());
        map.put("title", c.getTitle());
        map.put("category", c.getCategory());
        map.put("instructor", c.getInstructor());
        map.put("description", c.getDescription());
        map.put("enrolledCount", c.getEnrolledCount());

        List<Map<String, String>> enrolledStudents = new ArrayList<>();
        for (Student s : c.getEnrolledStudents()) {
            Map<String, String> sMap = new HashMap<>();
            sMap.put("studentId", s.getId());
            sMap.put("fullName", s.getFullName());
            sMap.put("email", s.getEmail());
            sMap.put("major", s.getMajor());
            enrolledStudents.add(sMap);
        }
        map.put("enrolledStudents", enrolledStudents);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getStudents() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : service.getAllStudents()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("studentId", s.getId());
            map.put("studentNumber", s.getStudentNumber());
            map.put("fullName", s.getFullName());
            map.put("email", s.getEmail());
            map.put("major", s.getMajor());
            map.put("activeEnrollmentsCount", s.getActiveEnrollmentCount());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") String id) {
        Student s = service.findStudentById(id);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("studentId", s.getId());
        map.put("studentNumber", s.getStudentNumber());
        map.put("fullName", s.getFullName());
        map.put("email", s.getEmail());
        map.put("major", s.getMajor());

        List<Map<String, String>> enrolledCourses = new ArrayList<>();
        for (Course c : s.getEnrolledCourses()) {
            Map<String, String> cMap = new HashMap<>();
            cMap.put("courseId", c.getCourseId());
            cMap.put("courseCode", c.getCourseCode());
            cMap.put("title", c.getTitle());
            cMap.put("instructor", c.getInstructor());
            enrolledCourses.add(cMap);
        }
        map.put("enrolledCourses", enrolledCourses);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<Map<String, Object>>> getEnrollments() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Enrollment e : service.getAllEnrollments()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("enrollmentId", e.getEnrollmentId());
            map.put("studentId", e.getStudent() != null ? e.getStudent().getId() : null);
            map.put("studentName", e.getStudent() != null ? e.getStudent().getFullName() : null);
            map.put("courseId", e.getCourse() != null ? e.getCourse().getCourseId() : null);
            map.put("courseTitle", e.getCourse() != null ? e.getCourse().getTitle() : null);
            map.put("enrollmentDate", e.getEnrollmentDate());
            map.put("status", e.getStatus());
            map.put("grade", e.getGrade());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }
}
