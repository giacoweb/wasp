package it.unipd.dei.webapp.servlet.page;

import jakarta.servlet.annotation.WebServlet;
import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "LogoutServlet", value = "/auth/logout")
public class LogoutServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(LogoutServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object user = session.getAttribute("authenticatedUser");
            session.invalidate();
            if (user != null) {
                logger.log(Level.INFO, "User logged out: {0}", user);
            }
        }

        response.sendRedirect(request.getContextPath() + "/login");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Allow", "POST");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Use POST to log out.");
    }
}
