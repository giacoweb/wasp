package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.exception.UserAlreadyExistsException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = {"/register", "/auth/register"})
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String error = request.getParameter("error");

        if ("email_exists".equals(error)) {
            request.setAttribute("errorMessage", "Email is already registered.");
        } else if ("validation".equals(error)) {
            request.setAttribute("errorMessage", "Please fill in all required fields.");
        } else if ("server_error".equals(error)) {
            request.setAttribute("errorMessage", "Unable to complete registration right now.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String name = request.getParameter("name");
        final String surname = request.getParameter("surname");
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");

        try {
            User user = userService.register(name, surname, email, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("authenticatedUser", user);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (UserAlreadyExistsException e) {
            response.sendRedirect(request.getContextPath() + "/register?error=email_exists");

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/register?error=validation");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/register?error=server_error");
        }
    }
}