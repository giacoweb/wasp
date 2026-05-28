package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.servlet.AbstractPageServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "GuestCompoundServlet", value = "/guest/compound")
public class GuestCompoundServlet extends AbstractPageServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/guest/compound.jsp").forward(request, response);
    }
}
