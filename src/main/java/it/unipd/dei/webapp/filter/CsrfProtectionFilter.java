package it.unipd.dei.webapp.filter;

import it.unipd.dei.webapp.util.CsrfUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;

@WebFilter(filterName = "CsrfProtectionFilter", urlPatterns = {
        "/login",
        "/register",
        "/auth/login",
        "/auth/register",
        "/dashboard",
        "/wallets",
        "/wallets/*",
        "/pac",
        "/pac/*",
        "/api/*",
        "/auth/logout"
})
public class CsrfProtectionFilter extends HttpFilter implements Filter {

    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute("authenticatedUser") != null;
        boolean publicAuthRoute = isPublicAuthRoute(request);

        if (!authenticated && !publicAuthRoute) {
            chain.doFilter(request, response);
            return;
        }

        if (session == null) {
            session = request.getSession(true);
        }

        String csrfToken = CsrfUtil.getOrCreateToken(session);
        request.setAttribute(CsrfUtil.REQUEST_ATTRIBUTE, csrfToken);
        response.setHeader(CsrfUtil.HEADER_NAME, csrfToken);

        if (isSafeMethod(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String providedToken = resolveProvidedToken(request);
        if (!CsrfUtil.tokensMatch(csrfToken, providedToken)) {
            reject(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isSafeMethod(final String method) {
        return "GET".equalsIgnoreCase(method)
                || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method)
                || "TRACE".equalsIgnoreCase(method);
    }

    private String resolveProvidedToken(final HttpServletRequest request) {
        if (isApiRequest(request)) {
            return request.getHeader(CsrfUtil.HEADER_NAME);
        }
        return request.getParameter(CsrfUtil.PARAMETER_NAME);
    }

    private boolean isApiRequest(final HttpServletRequest request) {
        String path = request.getServletPath();
        return path != null && path.startsWith("/api/");
    }

    private boolean isPublicAuthRoute(final HttpServletRequest request) {
        String path = request.getServletPath();
        return "/login".equals(path)
                || "/register".equals(path)
                || "/auth/login".equals(path)
                || "/auth/register".equals(path);
    }

    private void reject(final HttpServletRequest request,
                        final HttpServletResponse response) throws IOException {
        if (isApiRequest(request)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new JSONObject()
                    .put("error", "CSRF validation failed.")
                    .toString());
            return;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF validation failed.");
    }
}
