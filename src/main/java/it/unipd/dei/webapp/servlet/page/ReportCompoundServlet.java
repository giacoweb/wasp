package it.unipd.dei.webapp.servlet.page;


import it.unipd.dei.webapp.model.CompoundResult;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReportCompoundServlet", value = {"/report/compound"})
public class ReportCompoundServlet extends HttpServlet{

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
            request.setAttribute("compoundList",compoundList);
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/compoundreport.jsp").forward(request, response);

        }
        catch(ValidationException e)
        {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);

        }
        catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Please enter valid numbers.");
            request.getRequestDispatcher("/WEB-INF/jsp/calculator/calculator.jsp").forward(request, response);
        }
    }
}
