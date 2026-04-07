package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.service.WalletService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "WalletListServlet", value = "/wallets")
public class WalletListServlet extends HttpServlet {

    private WalletService walletService;

    @Override
    public void init() throws ServletException {
        this.walletService = new WalletService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("authenticatedUser");

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
            List<Wallet> wallets = walletService.getWalletsByUser(user.getId());
            request.setAttribute("wallets", wallets);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load wallets.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/wallets/list.jsp").forward(request, response);
    }
}
