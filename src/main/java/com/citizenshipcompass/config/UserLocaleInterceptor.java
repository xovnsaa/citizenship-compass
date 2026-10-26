package com.citizenshipcompass.config;

import com.citizenshipcompass.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Component
public class UserLocaleInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public UserLocaleInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getParameter("lang") != null) {
            return true;
        }

        Object explicitlySelectedLocale = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        if (explicitlySelectedLocale != null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                String language = userService.getUserByEmail(authentication.getName()).getPreferredLanguage();
                Locale locale = Locale.forLanguageTag(language);
                LocaleContextHolder.setLocale(locale);
                request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
            } catch (UsernameNotFoundException ignored) {
                // Test or externally managed authenticated users can safely use the default locale.
            }
        }
        return true;
    }
}
