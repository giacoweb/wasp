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
        "/wallets/*",
        "/forecast/*",
        "/portfolio/*"
})
public class AuthenticationFilter extends HttpFilter implements Filter {

    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        boolean authenticated = session != null && session.getAttribute("authenticatedUser") != null;

        if (!authenticated) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}