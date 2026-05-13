package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.WalletHasTransactionsException;
import it.unipd.dei.webapp.service.exception.WalletNotFoundException;
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

@WebServlet(name = "WalletDeleteServlet", value = "/wallets/delete")
public class WalletDeleteServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(WalletDeleteServlet.class.getName());

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

        try {
            if (walletId == null || walletId.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/wallets?error=validation");
                return;
            }

            UUID id = UUID.fromString(walletId);
            walletService.deleteWallet(id, user.getId());
            response.sendRedirect(request.getContextPath() + "/wallets?success=deleted");

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=validation");

        } catch (WalletNotFoundException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=not_found");

        } catch (UnauthorizedOperationException e) {
            logger.log(Level.WARNING, "User {0} attempted to delete wallet {1} owned by another user",
                    new Object[]{user.getId(), walletId});
            response.sendRedirect(request.getContextPath() + "/wallets?error=unauthorized");

        } catch (WalletHasTransactionsException e) {
            response.sendRedirect(request.getContextPath() + "/wallets?error=has_transactions");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to delete wallet " + walletId + " for user " + user.getId(), e);
            response.sendRedirect(request.getContextPath() + "/wallets?error=server_error");
        }
    }
}
