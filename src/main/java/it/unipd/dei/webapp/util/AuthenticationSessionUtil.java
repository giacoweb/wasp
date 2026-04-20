package it.unipd.dei.webapp.util;

import it.unipd.dei.webapp.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Objects;

public final class AuthenticationSessionUtil {

    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";

    private AuthenticationSessionUtil() {
        // Utility class
    }

    public static void startAuthenticatedSession(final HttpServletRequest request, final User user) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(user, "user");

        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession authenticatedSession = request.getSession(true);
        authenticatedSession.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, user);
    }
}
