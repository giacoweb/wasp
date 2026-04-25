package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.PacPlanService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PacEditServlet", value = "/pac/edit")
public class PacEditServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(PacEditServlet.class.getName());

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

        final String planId = request.getParameter("planId");
        final String assetName = request.getParameter("assetName");
        final String monthlyAmount = request.getParameter("monthlyAmount");
        final String targetAmount = request.getParameter("targetAmount");
        final String startDate = request.getParameter("startDate");
        final String active = request.getParameter("active");

        try {
            if (planId == null || planId.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/pac?error=validation");
                return;
            }

            UUID id = UUID.fromString(planId);
            pacPlanService.updatePlan(id, user.getId(), assetName, monthlyAmount, targetAmount, startDate, active);
            response.sendRedirect(request.getContextPath() + "/pac?success=updated");

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/pac?error=validation");

        } catch (ValidationException e) {
            String errorCode = e.getMessage() != null && e.getMessage().contains("not found")
                    ? "not_found" : "validation";
            response.sendRedirect(request.getContextPath() + "/pac?error=" + errorCode);

        } catch (UnauthorizedOperationException e) {
            logger.log(Level.WARNING, "User {0} attempted to edit PAC plan {1} owned by another user",
                    new Object[]{user.getId(), planId});
            response.sendRedirect(request.getContextPath() + "/pac?error=unauthorized");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update PAC plan " + planId + " for user " + user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/pac?error=server_error");
        }
    }
}
