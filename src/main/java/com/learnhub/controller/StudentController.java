package com.learnhub.controller;

import com.learnhub.model.Course;
import com.learnhub.model.Enrollment;
import com.learnhub.model.Student;
import com.learnhub.model.User;
import com.learnhub.service.LearnHubService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Student Portal actions:
 * - Browsing courses
 * - Enrolling in multiple courses (Many-to-Many via reference passing)
 * - Viewing enrolled courses
 * - Dropping courses (reference cleanup)
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    private final LearnHubService service;

    @Autowired
    public StudentController(LearnHubService service) {
        this.service = service;
    }

    private Student getAuthenticatedStudent(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user instanceof Student) {
            // Retrieve fresh state from service
            return service.findStudentById(user.getId());
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = getAuthenticatedStudent(session);
        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", student.getEnrolledCourses());
        model.addAttribute("allCourses", service.getAllCourses());
        model.addAttribute("categories", service.getAllCategories());
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String browseCourses(@RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "category", required = false) String category,
                                HttpSession session,
                                Model model) {
        Student student = getAuthenticatedStudent(session);
        if (student == null) {
            return "redirect:/login";
        }

        ArrayList<Course> courseList = service.searchCourses(search, category);
        model.addAttribute("student", student);
        model.addAttribute("courses", courseList);
        model.addAttribute("categories", service.getAllCategories());
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentCategory", category);
        return "student/courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(HttpSession session, Model model) {
        Student student = getAuthenticatedStudent(session);
        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);
        model.addAttribute("enrollments", student.getEnrollments());
        model.addAttribute("enrolledCourses", student.getEnrolledCourses());
        return "student/my-courses";
    }

    @PostMapping("/enroll")
    public String enrollInCourse(@RequestParam("courseId") String courseId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Student student = getAuthenticatedStudent(session);
        if (student == null) {
            return "redirect:/login";
        }

        Course course = service.findCourseById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/student/courses";
        }

        try {
            // OBJECT REFERENCE PASSING:
            // The service passes the live student and course object references to create the Enrollment
            Enrollment enrollment = service.enroll(student.getId(), course.getCourseId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully enrolled in \"" + course.getTitle() + "\"! (Enrollment ID: " + enrollment.getEnrollmentId() + ")");
            return "redirect:/student/my-courses";
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/courses";
        }
    }

    @PostMapping("/drop")
    public String dropCourse(@RequestParam("courseId") String courseId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Student student = getAuthenticatedStudent(session);
        if (student == null) {
            return "redirect:/login";
        }

        Course course = service.findCourseById(courseId);
        String courseTitle = (course != null) ? course.getTitle() : "the course";

        boolean success = service.dropCourse(student.getId(), courseId);
        if (success) {
            redirectAttributes.addFlashAttribute("infoMessage",
                    "You have successfully dropped \"" + courseTitle + "\". The references have been cleaned up dynamically.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to drop course or you were not enrolled.");
        }

        return "redirect:/student/my-courses";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Student student = getAuthenticatedStudent(session);
        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);
        model.addAttribute("enrollmentCount", student.getActiveEnrollmentCount());
        return "student/profile";
    }
}
