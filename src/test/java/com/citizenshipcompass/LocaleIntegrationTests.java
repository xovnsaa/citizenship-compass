package com.citizenshipcompass;

import com.citizenshipcompass.dto.RegistrationForm;
import com.citizenshipcompass.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocaleIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void publicPagesCanBeChangedToSpanish() throws Exception {
        mockMvc.perform(get("/").param("lang", "es"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Estudie para el examen")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"es\"")));
    }

    @Test
    void signedInUserAutomaticallyUsesSavedSpanishPreference() throws Exception {
        RegistrationForm form = new RegistrationForm();
        form.setFirstName("María");
        form.setLastName("Estudiante");
        form.setEmail("spanish@example.com");
        form.setPassword("password123");
        form.setPreferredLanguage("es");
        userService.registerUser(form);

        mockMvc.perform(get("/dashboard").with(user("spanish@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Panel principal")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Cerrar sesión")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"es\"")));

        assertSpanishPage("/questions", "Preguntas de educación cívica");
        assertSpanishPage("/quiz", "Enviar prueba");
        assertSpanishPage("/notes", "Aún no tiene notas de estudio");
        assertSpanishPage("/notes/new", "Guardar nota");
        assertSpanishPage("/favorites", "Guardadas para después");
        assertSpanishPage("/reports/progress", "Informe de progreso de práctica de ciudadanía");
    }

    @Test
    void explicitSpanishChoiceOverridesAccountsEnglishPreference() throws Exception {
        RegistrationForm form = new RegistrationForm();
        form.setFirstName("English");
        form.setLastName("Account");
        form.setEmail("english@example.com");
        form.setPassword("password123");
        form.setPreferredLanguage("en");
        userService.registerUser(form);

        mockMvc.perform(get("/dashboard")
                        .sessionAttr(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
                                Locale.forLanguageTag("es"))
                        .with(user("english@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Panel principal")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Cerrar sesión")));
    }

    private void assertSpanishPage(String path, String expectedText) throws Exception {
        mockMvc.perform(get(path).with(user("spanish@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(expectedText)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("lang=\"es\"")));
    }
}
