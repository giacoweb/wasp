package it.unipd.dei.webapp.filter;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Shared base class for FinTrack filters.
 */
public abstract class AbstractAppFilter extends HttpFilter implements AppFilter {

    protected HttpSession getSession(final HttpServletRequest request, final boolean create) {
        return request.getSession(create);
    }

    protected boolean isAuthenticated(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("authenticatedUser") != null;
    }

    protected void sendJsonUnauthorized(final HttpServletResponse response, final String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
