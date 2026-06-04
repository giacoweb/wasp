package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "GuestBudgetServlet", value = "/guest/budget")
public class GuestBudgetServlet extends AbstractPageServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/guest/budget.jsp").forward(request, response);
    }
}
