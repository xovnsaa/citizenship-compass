package com.citizenshipcompass;

import com.citizenshipcompass.dto.RegistrationForm;
import com.citizenshipcompass.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegistrationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void registrationRejectsInvalidInput() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "Learner")
                        .param("email", "not-an-email")
                        .param("password", "short")
                        .param("preferredLanguage", "en"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors(
                        "registrationForm", "firstName", "email", "password"));
    }

    @Test
    void registrationRejectsDuplicateEmailIgnoringCase() throws Exception {
        userService.registerUser(registrationForm("learner@example.com"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "Second")
                        .param("lastName", "Learner")
                        .param("email", "LEARNER@example.com")
                        .param("password", "password123")
                        .param("preferredLanguage", "en"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"));
    }

    @Test
    void validRegistrationRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "New")
                        .param("lastName", "Learner")
                        .param("email", "new@example.com")
                        .param("password", "password123")
                        .param("preferredLanguage", "en"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?lang=en"))
                .andExpect(flash().attributeExists("registrationSuccess"));
    }

    private RegistrationForm registrationForm(String email) {
        RegistrationForm form = new RegistrationForm();
        form.setFirstName("Test");
        form.setLastName("Learner");
        form.setEmail(email);
        form.setPassword("password123");
        form.setPreferredLanguage("en");
        return form;
    }
}
