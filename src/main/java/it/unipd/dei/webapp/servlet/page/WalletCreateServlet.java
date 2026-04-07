package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "WalletCreateServlet", value = "/wallets/create")
public class WalletCreateServlet extends HttpServlet {

    private WalletService walletService;

    @Override
    public void init() throws ServletException {
        this.walletService = new WalletService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("authenticatedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        final String name = request.getParameter("name");
        final String type = request.getParameter("type");
        final String currency = request.getParameter("currency");

        try {
            walletService.createWallet(user.getId(), name, type, currency);
            response.sendRedirect(request.getContextPath() + "/wallets?success=created");

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=validation");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=server_error");
        }
    }
}
