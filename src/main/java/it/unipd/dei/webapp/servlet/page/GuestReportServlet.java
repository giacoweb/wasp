package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.ReportSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "GuestReportServlet", value = "/guest/report")
public class GuestReportServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GuestReportServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        UUID token = session != null ? (UUID) session.getAttribute("guestSessionToken") : null;

        String error = request.getParameter("error");
        if ("no_data".equals(error)) {
            request.setAttribute("errorMessage", "Run at least one calculator before exporting the PDF.");
        }

        boolean hasGuestReportData = false;
        if (token != null) {
            try {
                ReportSession reportSession = new ReportSessionDAO().findBySessionToken(token);
                hasGuestReportData = reportSession != null
                        && reportSession.getReportJson() != null
                        && !reportSession.getReportJson().equals("{}");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to load guest report session.", e);
                throw new ServletException("Unable to load guest report session.", e);
            }
        }

        request.setAttribute("hasGuestReportData", hasGuestReportData);
        request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);
    }
}
