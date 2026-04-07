package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "WalletEditServlet", value = "/wallets/edit")
public class WalletEditServlet extends HttpServlet {

    private WalletService walletService;

    @Override
    public void init() throws ServletException {
        this.walletService = new WalletService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("authenticatedUser");

        final String walletId = request.getParameter("walletId");
        final String name = request.getParameter("name");
        final String type = request.getParameter("type");
        final String currency = request.getParameter("currency");

        try {
            UUID id = UUID.fromString(walletId);
            walletService.updateWallet(id, user.getId(), name, type, currency);
            response.sendRedirect(request.getContextPath() + "/wallets?success=updated");

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=validation");

        } catch (UnauthorizedOperationException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=unauthorized");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=server_error");
        }
    }
}
