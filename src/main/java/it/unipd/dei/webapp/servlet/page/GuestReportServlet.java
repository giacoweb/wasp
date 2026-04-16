package it.unipd.dei.webapp.servlet.page;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "GuestReportServlet", value = "/guest/report")
public class GuestReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        UUID token = session != null ? (UUID) session.getAttribute("guestSessionToken") : null;

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        //response.getWriter().write("Guest session token: " + token);

        String error = request.getParameter("error");
        if (error != null)
        {
            if(error.equals("loan_error_principal")){
                request.setAttribute("errorMessage", "Loan: The capital need to be greater than 0.");
            } else if(error.equals("loan_error_annualRate")){
                request.setAttribute("errorMessage", "Loan: The interest need to be between 0 and 100.");
            }else if(error.equals("loan_error_durations")){
                request.setAttribute("errorMessage", "Loan: The duration need to be greater than 0.");
            }

        }




        request.getRequestDispatcher("/WEB-INF/jsp/guestcalculator.jsp").forward(request, response);
    }
}