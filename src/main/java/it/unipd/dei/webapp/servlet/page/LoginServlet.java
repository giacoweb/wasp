package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.exception.InvalidCredentialsException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import it.unipd.dei.webapp.util.AuthenticationSessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = {"/login", "/auth/login"})
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String error = request.getParameter("error");

        if ("invalid_credentials".equals(error)) {
            request.setAttribute("errorMessage", "Invalid email or password.");
        } else if ("validation".equals(error)) {
            request.setAttribute("errorMessage", "Please enter email and password.");
        } else if ("server_error".equals(error)) {
            request.setAttribute("errorMessage", "Unable to complete login right now.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");

        try {
            User user = userService.login(email, password);

            AuthenticationSessionUtil.startAuthenticatedSession(request, user);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/login?error=validation");

        } catch (InvalidCredentialsException e) {
            response.sendRedirect(request.getContextPath() + "/login?error=invalid_credentials");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login?error=server_error");
        }
    }
}
