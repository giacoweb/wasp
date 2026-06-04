package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.PacPlan;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.PacPlanService;
import it.unipd.dei.webapp.view.PacPlanView;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PacListServlet", value = "/pac")
public class PacListServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(PacListServlet.class.getName());

    private PacPlanService pacPlanService;

    @Override
    public void init() throws ServletException {
        this.pacPlanService = new PacPlanService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String error = request.getParameter("error");

        if ("validation".equals(error)) {
            request.setAttribute("errorMessage", "Please fill in all required fields correctly.");
        } else if ("unauthorized".equals(error)) {
            request.setAttribute("errorMessage", "You are not authorized to perform this operation.");
        } else if ("not_found".equals(error)) {
            request.setAttribute("errorMessage", "PAC plan not found.");
        } else if ("server_error".equals(error)) {
            request.setAttribute("errorMessage", "An unexpected error occurred.");
        }

        String success = request.getParameter("success");

        if ("created".equals(success)) {
            request.setAttribute("successMessage", "PAC plan created successfully.");
        } else if ("updated".equals(success)) {
            request.setAttribute("successMessage", "PAC plan updated successfully.");
        } else if ("deleted".equals(success)) {
            request.setAttribute("successMessage", "PAC plan deleted successfully.");
        }

        try {
            List<PacPlan> plans = pacPlanService.getPlansByUser(user.getId());

            LocalDate today = LocalDate.now();
            List<PacPlanView> planViews = new ArrayList<>(plans.size());
            int activeCount = 0;
            BigDecimal monthlyCommitment = BigDecimal.ZERO;
            BigDecimal combinedTarget = BigDecimal.ZERO;

            for (PacPlan p : plans) {
                planViews.add(new PacPlanView(p, today));
                if (p.isActive()) {
                    activeCount++;
                    if (p.getMonthlyAmount() != null) {
                        monthlyCommitment = monthlyCommitment.add(p.getMonthlyAmount());
                    }
                }
                if (p.getTargetAmount() != null) {
                    combinedTarget = combinedTarget.add(p.getTargetAmount());
                }
            }

            request.setAttribute("planViews", planViews);
            request.setAttribute("activeCount", activeCount);
            request.setAttribute("monthlyCommitmentFormatted", String.format("%,.2f", monthlyCommitment));
            request.setAttribute("combinedTargetFormatted", String.format("%,.2f", combinedTarget));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load PAC plans for user " + user.getId(), e);
            request.setAttribute("errorMessage", "Unable to load PAC plans.");
            request.setAttribute("planViews", new ArrayList<PacPlanView>());
            request.setAttribute("activeCount", 0);
            request.setAttribute("monthlyCommitmentFormatted", "0.00");
            request.setAttribute("combinedTargetFormatted", "0.00");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/pac/list.jsp").forward(request, response);
    }
}
