package it.unipd.dei.webapp.servlet.page;


import it.unipd.dei.webapp.model.BudgetResult;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@WebServlet(name = "ReportBudgetServlet", value = "/report/budget")
public class ReportBudgetServlet extends HttpServlet {

    private CalculatorService calculator;

    @Override
    public void init() throws ServletException{
        this.calculator = new CalculatorService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/guest/report");

    }




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] incomeCategory = request.getParameterValues("incomeCategory");
        String[] incomeAmountStrings = request.getParameterValues("incomeAmount");
        String[] expenseCategory = request.getParameterValues("expenseCategory");
        String[] expenseAmountStrings = request.getParameterValues("expenseAmount");

        try {

            double[] incomeAmount = new double[incomeAmountStrings.length];
            for (int i = 0; i < incomeAmountStrings.length; i++) {
                incomeAmount[i] = Double.parseDouble(incomeAmountStrings[i]);
            }

            double[] expenseAmount = new double[expenseAmountStrings.length];
            for (int i = 0; i < expenseAmountStrings.length; i++) {
                expenseAmount[i] = Double.parseDouble(expenseAmountStrings[i]);
            }

            BudgetResult result = calculator.calculateBudget(incomeCategory,incomeAmount,expenseCategory,expenseAmount);
            request.setAttribute("budgetResult",result);
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/budgetreport.jsp").forward(request, response);

        } catch (ValidationException e) {

            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);

        }
        catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Please enter valid numbers.");
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);
        }
    }
}
