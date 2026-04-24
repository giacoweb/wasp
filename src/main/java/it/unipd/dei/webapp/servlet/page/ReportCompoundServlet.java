package it.unipd.dei.webapp.servlet.page;


import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.CompoundResult;
import it.unipd.dei.webapp.model.ReportSession;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ReportCompoundServlet", value = {"/report/compound"})
public class ReportCompoundServlet extends HttpServlet{

    private static final Logger logger = Logger.getLogger(ReportCompoundServlet.class.getName());

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

            double initialCapital = Double.parseDouble(request.getParameter("initialCapital"));
            double monthlyContribution = Double.parseDouble(request.getParameter("monthlyContribution"));
            double annualRate = Double.parseDouble(request.getParameter("annualRate"));
            int years = Integer.parseInt(request.getParameter("years"));

            List<CompoundResult> compoundList = calculator.calculateCompound(initialCapital,monthlyContribution,annualRate,years);

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

            JSONObject compoundData = new JSONObject();
            compoundData.put("initialCapital", initialCapital);
            compoundData.put("monthlyContribution", monthlyContribution);
            compoundData.put("annualRate", annualRate);
            compoundData.put("years", years);
            JSONArray arr = new JSONArray();

            for (CompoundResult cr : compoundList) {
                JSONObject r = new JSONObject();
                r.put("year", cr.getYear());
                r.put("balance", cr.getTotalValue().toPlainString());
                arr.put(r);
            }
            compoundData .put("projection", arr);
            rootObject.put("compound",compoundData);
            reportDao.updateReportSessionJson(guestToken,rootObject.toString());




            request.setAttribute("compoundList",compoundList);
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/compoundreport.jsp").forward(request, response);

        }
        catch(ValidationException e) {

            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);
        }
        catch (NumberFormatException e) {

            request.setAttribute("errorMessage", "Please enter valid numbers.");
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error computing compound interest", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL Error.");
        }
    }
}
