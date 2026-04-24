package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.PacPlanService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PacCreateServlet", value = "/pac/create")
public class PacCreateServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PacCreateServlet.class.getName());

    private PacPlanService pacPlanService;

    @Override
    public void init() throws ServletException {
        this.pacPlanService = new PacPlanService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        final String assetName = request.getParameter("assetName");
        final String monthlyAmount = request.getParameter("monthlyAmount");
        final String targetAmount = request.getParameter("targetAmount");
        final String startDate = request.getParameter("startDate");

        try {
            pacPlanService.createPlan(user.getId(), assetName, monthlyAmount, targetAmount, startDate);
            response.sendRedirect(request.getContextPath() + "/pac?success=created");

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/pac?error=validation");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create PAC plan for user " + user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/pac?error=server_error");
        }
    }
}
