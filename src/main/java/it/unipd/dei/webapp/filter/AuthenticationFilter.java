package it.unipd.dei.webapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/dashboard",
        "/transactions/*",
        "/wallets",
        "/wallets/*",
        "/forecast/*",
        "/portfolio/*",
        "/api/*"
})
public class AuthenticationFilter extends HttpFilter implements Filter {

    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        boolean authenticated = session != null && session.getAttribute("authenticatedUser") != null;

        if (!authenticated) {
            String path = request.getServletPath();
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