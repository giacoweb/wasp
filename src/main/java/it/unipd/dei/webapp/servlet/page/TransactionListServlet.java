package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "TransactionListServlet", value = "/transactions")
public class TransactionListServlet extends AbstractPageServlet {

    private static final Logger logger = Logger.getLogger(TransactionListServlet.class.getName());

    private WalletService walletService;

    @Override
    public void init() throws ServletException {
        this.walletService = new WalletService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Wallet> wallets = walletService.getWalletsByUser(user.getId());
            request.setAttribute("wallets", wallets);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load wallets for user " + user.getId(), e);
            request.setAttribute("errorMessage", "Unable to load wallets.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/transactions/list.jsp").forward(request, response);
    }
}
