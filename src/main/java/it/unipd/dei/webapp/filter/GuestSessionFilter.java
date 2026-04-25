package it.unipd.dei.webapp.filter;

import it.unipd.dei.webapp.service.GuestSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@WebFilter(filterName = "GuestSessionFilter", urlPatterns = {"/guest/*", "/report/loan", "/report/compound", "/report/budget"})
public class GuestSessionFilter extends AbstractAppFilter {

    private GuestSessionService guestSessionService;

    @Override
    public void init() throws ServletException {
        this.guestSessionService = new GuestSessionService();
    }

    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession(true);
        Object tokenObject = session.getAttribute("guestSessionToken");

        UUID existingToken = null;
        if (tokenObject instanceof UUID) {
            existingToken = (UUID) tokenObject;
        }

        try {
            UUID guestToken = guestSessionService.ensureGuestSessionExists(existingToken);
            session.setAttribute("guestSessionToken", guestToken);

            chain.doFilter(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to initialize guest session.", e);
        }
    }
}
