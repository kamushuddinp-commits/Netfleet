package com.learnhub.controller;

import com.learnhub.model.User;
import com.learnhub.service.LearnHubService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for Landing page and Conceptual Documentation page.
 */
@Controller
public class HomeController {

    private final LearnHubService service;

    @Autowired
    public HomeController(LearnHubService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("metrics", service.getSystemMetrics());
        model.addAttribute("featuredCourses", service.getAllCourses());
        return "index";
    }

    @GetMapping("/concept")
    public String showConceptPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("students", service.getAllStudents());
        model.addAttribute("courses", service.getAllCourses());
        model.addAttribute("enrollments", service.getAllEnrollments());
        return "concept-map";
    }
}
