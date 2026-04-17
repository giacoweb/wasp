package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.DashboardSummary;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DashboardServlet", value = "/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DashboardServlet.class.getName());

    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {
        this.dashboardService = new DashboardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            DashboardSummary summary = dashboardService.getSummary(user.getId());
            request.setAttribute("summary", summary);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load dashboard data for user " + user.getId(), e);
            request.setAttribute("errorMessage", "Unable to load dashboard data.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/dashboard.jsp").forward(request, response);
    }
}
