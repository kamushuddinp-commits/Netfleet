package com.learnhub.controller;

import com.learnhub.model.Student;
import com.learnhub.model.User;
import com.learnhub.service.LearnHubService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles Authentication: Login, Registration, and Session management.
 */
@Controller
public class AuthController {

    private final LearnHubService service;

    @Autowired
    public AuthController(LearnHubService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "role", required = false) String role,
                                @RequestParam(value = "email", required = false) String email,
                                Model model,
                                HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
        }
        if (email != null && !email.isEmpty()) {
            model.addAttribute("prefillEmail", email);
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = service.authenticate(email, password);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email address or password. Please try again.");
            return "redirect:/login";
        }

        session.setAttribute("currentUser", user);
        redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFullName() + "!");

        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("fullName") String fullName,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam(value = "major", defaultValue = "Computer Science") String major,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Student student = service.registerStudent(fullName, email, password, major);
            session.setAttribute("currentUser", student);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome to LearnHub, " + student.getFullName() + "! Your account has been registered successfully.");
            return "redirect:/student/dashboard";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("infoMessage", "You have been signed out successfully.");
        return "redirect:/login";
    }
}
