package it.unipd.dei.webapp.servlet.page;


import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.ReportSession;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.model.AmortizationRow;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ReportLoanServlet", value = {"/report/loan"})
public class ReportLoanServlet extends HttpServlet{

    private static final Logger logger = Logger.getLogger(ReportLoanServlet.class.getName());

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

        try
        {
            double principal = Double.parseDouble(request.getParameter("principal"));
            double annualRate =Double.parseDouble(request.getParameter("annualRate"));
            int months = Integer.parseInt(request.getParameter("months"));

            List<AmortizationRow> amortizationList = calculator.calculateLoan(principal,annualRate,months);


            HttpSession httpSession = request.getSession(false);
            UUID guestToken = (UUID) httpSession.getAttribute("guestSessionToken");

            ReportSessionDAO reportDao = new ReportSessionDAO();

            ReportSession sessioneAttuale = reportDao.findBySessionToken(guestToken);
            String jsonVecchio = sessioneAttuale.getReportJson();
            JSONObject rootObject;

            if(jsonVecchio == null || jsonVecchio.equals("{}")) {
                rootObject = new JSONObject();
            } else {
                rootObject = new JSONObject(jsonVecchio);
            }

            JSONObject loanData = new JSONObject();
            loanData.put("principal", principal);
            loanData.put("annualRate", annualRate);
            loanData.put("months", months);
            JSONArray arr = new JSONArray();
            for (AmortizationRow row : amortizationList) {
                JSONObject r = new JSONObject();
                r.put("month",            row.getMonth());
                r.put("totalPayment",     row.getTotalPayment().toPlainString());
                r.put("principalPaid",    row.getPrincipalPaid().toPlainString());
                r.put("interestPaid",     row.getInterestPaid().toPlainString());
                r.put("remainingBalance", row.getRemainingBalance().toPlainString());
                arr.put(r);
            }
            loanData.put("schedule", arr);
            rootObject.put("loan",loanData);
            reportDao.updateReportSessionJson(guestToken,rootObject.toString());

            request.setAttribute("amortizationList",amortizationList);
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/loanreport.jsp").forward(request, response);

        }
        catch(ValidationException e) {

            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);
        }
        catch (NumberFormatException e) {

            request.setAttribute("errorMessage", "Please enter valid numbers.");
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);

        }
        catch(SQLException e) {
            logger.log(Level.SEVERE, "Database error computing loan amortization", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL Error.");
        }
    }
}
