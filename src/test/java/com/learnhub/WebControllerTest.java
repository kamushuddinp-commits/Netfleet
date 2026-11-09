package com.learnhub;

import com.learnhub.model.Student;
import com.learnhub.service.LearnHubService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LearnHubService service;

    @Test
    @DisplayName("GET / should return index page with HTTP 200 and metrics")
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("metrics"))
                .andExpect(model().attributeExists("featuredCourses"));
    }

    @Test
    @DisplayName("GET /concept should return the M:N ER architecture documentation page")
    public void testConceptPage() throws Exception {
        mockMvc.perform(get("/concept"))
                .andExpect(status().isOk())
                .andExpect(view().name("concept-map"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attributeExists("enrollments"));
    }

    @Test
    @DisplayName("GET /login should display login page")
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("POST /login with valid student credentials redirects to /student/dashboard")
    public void testStudentLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "alice@example.com")
                        .param("password", "pass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    @DisplayName("POST /login with valid admin credentials redirects to /admin/dashboard")
    public void testAdminLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "admin@learnhub.com")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("POST /register should create a new Student and redirect to dashboard")
    public void testStudentRegistration() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Test Student")
                        .param("email", "test.student@example.com")
                        .param("password", "secret123")
                        .param("major", "Data Science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    @DisplayName("Student course enrollment via web request creates M:N link")
    public void testStudentEnrollmentPost() throws Exception {
        Student bob = service.findStudentById("STU-102");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", bob);

        // Bob enrolls in Python course (CRS-102)
        mockMvc.perform(post("/student/enroll")
                        .session(session)
                        .param("courseId", "CRS-102"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/my-courses"));
    }

    @Test
    @DisplayName("GET /api/courses returns JSON course array")
    public void testApiCourses() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].title", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/stats returns system metrics JSON")
    public void testApiStats() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.totalCourses", greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.activeEnrollments", greaterThanOrEqualTo(1)));
    }
}
