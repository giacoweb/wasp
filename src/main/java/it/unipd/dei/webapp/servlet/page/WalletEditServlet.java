package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.WalletService;
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

@WebServlet(name = "WalletEditServlet", value = "/wallets/edit")
public class WalletEditServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(WalletEditServlet.class.getName());

    private WalletService walletService;

    @Override
    public void init() throws ServletException {
        this.walletService = new WalletService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        final String walletId = request.getParameter("walletId");
        final String name = request.getParameter("name");
        final String type = request.getParameter("type");
        final String currency = request.getParameter("currency");

        try {
            if (walletId == null || walletId.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/wallets?error=validation");
                return;
            }

            UUID id = UUID.fromString(walletId);
            walletService.updateWallet(id, user.getId(), name, type, currency);
            response.sendRedirect(request.getContextPath() + "/wallets?success=updated");

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=validation");

        } catch (ValidationException e) {
            String errorCode = e.getMessage() != null && e.getMessage().contains("not found")
                    ? "not_found" : "validation";
            response.sendRedirect(request.getContextPath() + "/wallets?error=" + errorCode);

        } catch (UnauthorizedOperationException e) {
            logger.log(Level.WARNING, "User {0} attempted to edit wallet {1} owned by another user",
                    new Object[]{user.getId(), walletId});
            response.sendRedirect(request.getContextPath() + "/wallets?error=unauthorized");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update wallet " + walletId + " for user " + user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/wallets?error=server_error");
        }
    }
}
