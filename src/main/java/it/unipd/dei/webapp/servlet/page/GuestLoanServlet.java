package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "GuestLoanServlet", value = "/guest/loan")
public class GuestLoanServlet extends AbstractPageServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/guest/loan.jsp").forward(request, response);
    }
}
