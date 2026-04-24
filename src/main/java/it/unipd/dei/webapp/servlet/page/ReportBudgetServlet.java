package it.unipd.dei.webapp.servlet.page;


import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.BudgetResult;
import it.unipd.dei.webapp.model.ReportSession;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet(name = "ReportBudgetServlet", value = "/report/budget")
public class ReportBudgetServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ReportBudgetServlet.class.getName());

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

            HttpSession httpSession = request.getSession(false);
            UUID guestToken = (UUID) httpSession.getAttribute("guestSessionToken");
            ReportSessionDAO reportDao = new ReportSessionDAO();

            ReportSession reportsession = reportDao.findBySessionToken(guestToken);
            String jsonOld = reportsession.getReportJson();
            JSONObject rootObject;

            if(jsonOld == null || jsonOld.equals("{}")) {
                rootObject = new JSONObject();
            } else {
                rootObject = new JSONObject(jsonOld);
            }

            JSONObject budgetData = new JSONObject();
            budgetData.put("totalIncome", result.getTotalIncome().toPlainString());
            budgetData.put("totalExpenses", result.getTotalExpenses().toPlainString());
            budgetData.put("net", result.getNetBalance().toPlainString());

            org.json.JSONObject incomeObj = new org.json.JSONObject();
            result.getIncomeByCategory().forEach((k, v) -> incomeObj.put(k, v.toPlainString()));
            budgetData.put("income", incomeObj);

            org.json.JSONObject expenseObj = new org.json.JSONObject();
            result.getExpensesByCategory().forEach((k, v) -> expenseObj.put(k, v.toPlainString()));
            budgetData.put("expenses", expenseObj);

            rootObject.put("budget",budgetData);
            reportDao.updateReportSessionJson(guestToken,rootObject.toString());


            request.setAttribute("budgetResult",result);
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/budgetreport.jsp").forward(request, response);

        } catch (ValidationException e) {

            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);

        }
        catch (NumberFormatException e) {

            request.setAttribute("errorMessage", "Please enter valid numbers.");
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error computing budget breakdown", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL Error.");
        }
    }
}
