package it.unipd.dei.webapp.servlet.page;


import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.model.AmortizationRow;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReportLoanServlet", value = {"/report/loan"})
public class ReportLoanServlet extends HttpServlet{

    private CalculatorService calculator;

    @Override
    public void init() throws ServletException{
        this.calculator = new CalculatorService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getContextPath());

    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        double principal = Double.parseDouble(request.getParameter("principal"));
        double annualRate =Double.parseDouble(request.getParameter("annualRate"));
        int months = Integer.parseInt(request.getParameter("months"));
        System.out.println(principal);
        System.out.println(annualRate);
        System.out.println(months);

        try
        {

            List<AmortizationRow> amortizationList = calculator.calculateLoan(principal,annualRate,months);
            request.setAttribute("amortizationList",amortizationList);
            //getServletContext().getRequestDispatcher("/WEB-INF/jsp/loanreport.jsp").forward(request,response);
            request.getRequestDispatcher("/WEB-INF/jsp/loanreport.jsp").forward(request, response);
        }
        catch(ValidationException e)
        {
            //getServletContext().getRequestDispatcher(request.getContextPath()+"/guest/report?error="+e.getMessage()).forward(request,response);
            response.sendRedirect(request.getContextPath()+"/guest/report?error="+e.getMessage());
        }



    }
}
