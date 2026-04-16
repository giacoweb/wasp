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



        request.getRequestDispatcher("/WEB-INF/jsp/guestcalculator.jsp").forward(request, response);
    }
}