package it.unipd.dei.webapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/dashboard",
        "/transactions/*",
        "/expenses",
        "/expenses/*",
        "/wallets",
        "/wallets/*",
        "/pac",
        "/pac/*",
        "/forecast/*",
        "/portfolio/*",
        "/api/*"
})
public class AuthenticationFilter extends AbstractAppFilter {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute("authenticatedUser") != null;

        if (!authenticated) {
            String path = request.getServletPath();
            logger.log(Level.FINE, "Unauthenticated access attempt to {0} from {1}",
                    new Object[]{path, request.getRemoteAddr()});
            if (path != null && path.startsWith("/api/")) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Authentication required.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return;
        }

        chain.doFilter(request, response);
    }
}
