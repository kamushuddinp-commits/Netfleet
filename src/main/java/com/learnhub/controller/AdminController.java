package com.learnhub.controller;

import com.learnhub.model.Admin;
import com.learnhub.model.Course;
import com.learnhub.model.Student;
import com.learnhub.model.User;
import com.learnhub.service.LearnHubService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for Admin Portal actions:
 * - Managing online courses (Add new courses, view capacity)
 * - Viewing enrolled students per course (Course -> Enrollment -> Student)
 * - Managing registered students and viewing their enrolled courses (Student -> Enrollment -> Course)
 * - System metrics & Many-to-Many ER reporting
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final LearnHubService service;

    @Autowired
    public AdminController(LearnHubService service) {
        this.service = service;
    }

    private Admin getAuthenticatedAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user instanceof Admin) {
            return (Admin) user;
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("metrics", service.getSystemMetrics());
        model.addAttribute("courses", service.getAllCourses());
        model.addAttribute("recentEnrollments", service.getRecentEnrollments(8));
        model.addAttribute("students", service.getAllStudents());
        return "admin/dashboard";
    }

    @GetMapping("/courses")
    public String listCourses(HttpSession session, Model model) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("courses", service.getAllCourses());
        model.addAttribute("categories", service.getAllCategories());
        return "admin/courses";
    }

    @PostMapping("/courses/new")
    public String createCourse(@RequestParam("code") String code,
                               @RequestParam("title") String title,
                               @RequestParam("category") String category,
                               @RequestParam("instructor") String instructor,
                               @RequestParam(value = "credits", defaultValue = "3") int credits,
                               @RequestParam(value = "capacity", defaultValue = "30") int capacity,
                               @RequestParam("description") String description,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        try {
            Course newCourse = service.createCourse(code, title, category, description, instructor, credits, capacity);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Course \"" + newCourse.getTitle() + "\" (" + newCourse.getCourseCode() + ") created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create course: " + e.getMessage());
        }

        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/{id}")
    public String viewCourseDetails(@PathVariable("id") String courseId,
                                    HttpSession session,
                                    Model model) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        Course course = service.findCourseById(courseId);
        if (course == null) {
            return "redirect:/admin/courses";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("course", course);
        // DEMONSTRATES REFERENCE TRAVERSAL:
        // Traversing dynamic ArrayList to get live Student references enrolled in this Course
        model.addAttribute("enrolledStudents", course.getEnrolledStudents());
        model.addAttribute("enrollments", course.getEnrollments());
        return "admin/course-details";
    }

    @GetMapping("/students")
    public String listStudents(HttpSession session, Model model) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("students", service.getAllStudents());
        return "admin/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudentDetails(@PathVariable("id") String studentId,
                                     HttpSession session,
                                     Model model) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        Student student = service.findStudentById(studentId);
        if (student == null) {
            return "redirect:/admin/students";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("student", student);
        // DEMONSTRATES REFERENCE TRAVERSAL:
        // Traversing dynamic ArrayList to get live Course references enrolled by this Student
        model.addAttribute("enrolledCourses", student.getEnrolledCourses());
        model.addAttribute("enrollments", student.getEnrollments());
        return "admin/student-details";
    }

    @GetMapping("/enrollments")
    public String listEnrollments(HttpSession session, Model model) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("enrollments", service.getAllEnrollments());
        return "admin/enrollments";
    }
}
