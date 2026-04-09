package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.PacPlanService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "PacDeleteServlet", value = "/pac/delete")
public class PacDeleteServlet extends HttpServlet {

    private PacPlanService pacPlanService;

    @Override
    public void init() throws ServletException {
        this.pacPlanService = new PacPlanService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        final String planId = request.getParameter("planId");

        try {
            if (planId == null || planId.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/pac?error=validation");
                return;
            }

            UUID id = UUID.fromString(planId);
            pacPlanService.deletePlan(id, user.getId());
            response.sendRedirect(request.getContextPath() + "/pac?success=deleted");

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/pac?error=validation");

        } catch (ValidationException e) {
            response.sendRedirect(request.getContextPath() + "/pac?error=not_found");

        } catch (UnauthorizedOperationException e) {
            response.sendRedirect(request.getContextPath() + "/pac?error=unauthorized");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/pac?error=server_error");
        }
    }
}
