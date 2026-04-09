package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.RecurringExpense;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.RecurringExpenseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ExpenseListServlet", value = "/expenses")
public class ExpenseListServlet extends HttpServlet {

    private RecurringExpenseService expenseService;

    @Override
    public void init() throws ServletException {
        this.expenseService = new RecurringExpenseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            request.setAttribute("errorMessage", "Recurring expense not found.");
        } else if ("server_error".equals(error)) {
            request.setAttribute("errorMessage", "An unexpected error occurred.");
        }

        String success = request.getParameter("success");
        if ("created".equals(success)) {
            request.setAttribute("successMessage", "Recurring expense created successfully.");
        } else if ("updated".equals(success)) {
            request.setAttribute("successMessage", "Recurring expense updated successfully.");
        } else if ("toggled".equals(success)) {
            request.setAttribute("successMessage", "Recurring expense status changed.");
        } else if ("deleted".equals(success)) {
            request.setAttribute("successMessage", "Recurring expense deleted successfully.");
        }

        try {
            List<RecurringExpense> expenses = expenseService.getExpensesByUser(user.getId());
            request.setAttribute("expenses", expenses);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load recurring expenses.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/expenses/list.jsp").forward(request, response);
    }
}
