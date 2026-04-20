package it.unipd.dei.webapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter(filterName = "SessionCookieHardeningFilter", urlPatterns = "/*")
public class SessionCookieHardeningFilter extends HttpFilter implements Filter {

    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String SAME_SITE_ATTRIBUTE = "SameSite";
    private static final String SAME_SITE_VALUE = "Lax";
    private static final String HTTP_ONLY_ATTRIBUTE = "HttpOnly";
    private static final String SECURE_ATTRIBUTE = "Secure";

    @Override
    protected void doFilter(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final FilterChain chain) throws IOException, ServletException {

        HttpServletResponse wrappedResponse =
                new SessionCookieResponseWrapper(response, request.isSecure());

        chain.doFilter(request, wrappedResponse);
    }

    static String hardenSessionCookieHeader(final String headerValue, final boolean secureRequest) {
        if (!isSessionCookieHeader(headerValue)) {
            return headerValue;
        }

        String hardened = headerValue;

        if (!hasAttribute(hardened, HTTP_ONLY_ATTRIBUTE)) {
            hardened += "; " + HTTP_ONLY_ATTRIBUTE;
        }

        if (!hasAttribute(hardened, SAME_SITE_ATTRIBUTE)) {
            hardened += "; " + SAME_SITE_ATTRIBUTE + "=" + SAME_SITE_VALUE;
        }

        if (secureRequest && !hasAttribute(hardened, SECURE_ATTRIBUTE)) {
            hardened += "; " + SECURE_ATTRIBUTE;
        }

        return hardened;
    }

    private static boolean isSessionCookieHeader(final String headerValue) {
        if (headerValue == null) {
            return false;
        }

        String trimmed = headerValue.trim();
        return trimmed.regionMatches(true, 0, SESSION_COOKIE_NAME + "=", 0, SESSION_COOKIE_NAME.length() + 1);
    }

    private static boolean hasAttribute(final String headerValue, final String attributeName) {
        String pattern = "(?i)(^|;\\s*)" + Pattern.quote(attributeName) + "(=|;|$)";
        return Pattern.compile(pattern).matcher(headerValue).find();
    }

    private static final class SessionCookieResponseWrapper extends HttpServletResponseWrapper {

        private final boolean secureRequest;

        private SessionCookieResponseWrapper(final HttpServletResponse response, final boolean secureRequest) {
            super(response);
            this.secureRequest = secureRequest;
        }

        @Override
        public void addCookie(final Cookie cookie) {
            if (cookie != null && SESSION_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
                cookie.setHttpOnly(true);
                cookie.setSecure(secureRequest);
            }

            super.addCookie(cookie);
        }

        @Override
        public void setHeader(final String name, final String value) {
            if (SET_COOKIE_HEADER.equalsIgnoreCase(name)) {
                super.setHeader(name, hardenSessionCookieHeader(value, secureRequest));
                return;
            }

            super.setHeader(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            if (SET_COOKIE_HEADER.equalsIgnoreCase(name)) {
                super.addHeader(name, hardenSessionCookieHeader(value, secureRequest));
                return;
            }

            super.addHeader(name, value);
        }
    }
}
