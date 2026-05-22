package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.WalletSummary;
import it.unipd.dei.webapp.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "WalletListServlet", value = "/wallets")
public class WalletListServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(WalletListServlet.class.getName());

    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {
        this.dashboardService = new DashboardService();
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
            request.setAttribute("errorMessage", "Wallet not found.");
        } else if ("has_transactions".equals(error)) {
            request.setAttribute("errorMessage", "Wallet cannot be deleted because it has linked transactions.");
        } else if ("server_error".equals(error)) {
            request.setAttribute("errorMessage", "An unexpected error occurred.");
        }

        String success = request.getParameter("success");

        if ("created".equals(success)) {
            request.setAttribute("successMessage", "Wallet created successfully.");
        } else if ("updated".equals(success)) {
            request.setAttribute("successMessage", "Wallet updated successfully.");
        } else if ("deleted".equals(success)) {
            request.setAttribute("successMessage", "Wallet deleted successfully.");
        }

        try {
            List<WalletSummary> wallets = dashboardService.getSummary(user.getId()).getWallets();
            request.setAttribute("wallets", wallets != null ? wallets : Collections.emptyList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load wallets for user " + user.getId(), e);
            request.setAttribute("errorMessage", "Unable to load wallets.");
            request.setAttribute("wallets", Collections.emptyList());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/wallets/list.jsp").forward(request, response);
    }
}
