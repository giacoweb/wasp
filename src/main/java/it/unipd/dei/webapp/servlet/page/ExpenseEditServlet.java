package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.RecurringExpenseService;
import it.unipd.dei.webapp.service.exception.RecurringExpenseNotFoundException;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "ExpenseEditServlet", value = "/expenses/edit")
public class ExpenseEditServlet extends HttpServlet {

    private RecurringExpenseService expenseService;

    @Override
    public void init() throws ServletException {
        this.expenseService = new RecurringExpenseService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("authenticatedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        final String expenseIdStr = request.getParameter("expenseId");
        final String label        = request.getParameter("label");
        final String amount       = request.getParameter("amount");
        final String periodicity  = request.getParameter("periodicity");
        final String nextDue      = request.getParameter("nextDue");

        try {
            if (expenseIdStr == null || expenseIdStr.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/expenses?error=validation");
                return;
            }

            UUID expenseId = UUID.fromString(expenseIdStr);
            expenseService.updateExpense(expenseId, user.getId(), label, amount, periodicity, nextDue);
            response.sendRedirect(request.getContextPath() + "/expenses?success=updated");

        } catch (IllegalArgumentException | ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=validation");

        } catch (RecurringExpenseNotFoundException e) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=not_found");

        } catch (UnauthorizedOperationException e) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=unauthorized");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=server_error");
        }
    }
}
