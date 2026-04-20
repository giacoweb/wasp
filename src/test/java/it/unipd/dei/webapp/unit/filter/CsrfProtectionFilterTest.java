package it.unipd.dei.webapp.unit.filter;

import it.unipd.dei.webapp.filter.CsrfProtectionFilter;
import it.unipd.dei.webapp.util.CsrfUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsrfProtectionFilterTest {

    @Test
    void loginGetCreatesTokenAndContinues() throws Exception {
        RequestState requestState = new RequestState("GET", "/login");
        HttpServletRequest request = requestProxy(requestState);
        ResponseState responseState = new ResponseState();
        HttpServletResponse response = responseProxy(responseState);
        ChainState chainState = new ChainState();
        FilterChain chain = chainProxy(chainState);

        Filter filter = new CsrfProtectionFilter();
        filter.doFilter(request, response, chain);

        assertTrue(chainState.called);
        assertNotNull(requestState.currentSessionState);
        String sessionToken = (String) requestState.currentSessionState.attributes.get(CsrfUtil.SESSION_ATTRIBUTE);
        assertNotNull(sessionToken);
        assertEquals(sessionToken, requestState.attributes.get(CsrfUtil.REQUEST_ATTRIBUTE));
        assertEquals(sessionToken, responseState.headers.get(CsrfUtil.HEADER_NAME));
    }

    @Test
    void loginPostWithoutTokenIsRejected() throws Exception {
        RequestState requestState = new RequestState("POST", "/auth/login");
        requestState.currentSessionState = new SessionState();
        requestState.currentSessionState.attributes.put(CsrfUtil.SESSION_ATTRIBUTE, "expected-token");
        requestState.currentSession = sessionProxy(requestState, requestState.currentSessionState);

        HttpServletRequest request = requestProxy(requestState);
        ResponseState responseState = new ResponseState();
        HttpServletResponse response = responseProxy(responseState);
        ChainState chainState = new ChainState();
        FilterChain chain = chainProxy(chainState);

        Filter filter = new CsrfProtectionFilter();
        filter.doFilter(request, response, chain);

        assertFalse(chainState.called);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, responseState.errorStatus);
        assertEquals("CSRF validation failed.", responseState.errorMessage);
    }

    @Test
    void loginPostWithMatchingTokenIsAllowed() throws Exception {
        RequestState requestState = new RequestState("POST", "/auth/login");
        requestState.parameters.put(CsrfUtil.PARAMETER_NAME, "expected-token");
        requestState.currentSessionState = new SessionState();
        requestState.currentSessionState.attributes.put(CsrfUtil.SESSION_ATTRIBUTE, "expected-token");
        requestState.currentSession = sessionProxy(requestState, requestState.currentSessionState);

        HttpServletRequest request = requestProxy(requestState);
        ResponseState responseState = new ResponseState();
        HttpServletResponse response = responseProxy(responseState);
        ChainState chainState = new ChainState();
        FilterChain chain = chainProxy(chainState);

        Filter filter = new CsrfProtectionFilter();
        filter.doFilter(request, response, chain);

        assertTrue(chainState.called);
        assertEquals(0, responseState.errorStatus);
    }

    private static HttpServletRequest requestProxy(final RequestState state) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();

                    if ("getMethod".equals(methodName)) {
                        return state.method;
                    }
                    if ("getServletPath".equals(methodName)) {
                        return state.servletPath;
                    }
                    if ("getParameter".equals(methodName)) {
                        return state.parameters.get(args[0]);
                    }
                    if ("getHeader".equals(methodName)) {
                        return state.headers.get(args[0]);
                    }
                    if ("setAttribute".equals(methodName)) {
                        state.attributes.put((String) args[0], args[1]);
                        return null;
                    }
                    if ("getAttribute".equals(methodName)) {
                        return state.attributes.get(args[0]);
                    }
                    if ("getSession".equals(methodName)) {
                        boolean create = args == null || args.length == 0 || Boolean.TRUE.equals(args[0]);
                        if (state.currentSession == null && create) {
                            state.currentSessionState = new SessionState();
                            state.currentSession = sessionProxy(state, state.currentSessionState);
                        }
                        return state.currentSession;
                    }

                    return defaultValue(proxy, methodName, method.getReturnType(), args);
                });
    }

    private static HttpSession sessionProxy(final RequestState requestState, final SessionState state) {
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();

                    if ("getAttribute".equals(methodName)) {
                        return state.attributes.get(args[0]);
                    }
                    if ("setAttribute".equals(methodName)) {
                        state.attributes.put((String) args[0], args[1]);
                        return null;
                    }
                    if ("invalidate".equals(methodName)) {
                        state.invalidated = true;
                        requestState.currentSession = null;
                        requestState.currentSessionState = null;
                        return null;
                    }

                    return defaultValue(proxy, methodName, method.getReturnType(), args);
                });
    }

    private static HttpServletResponse responseProxy(final ResponseState state) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();

                    if ("setHeader".equals(methodName)) {
                        state.headers.put((String) args[0], (String) args[1]);
                        return null;
                    }
                    if ("setContentType".equals(methodName)) {
                        state.contentType = (String) args[0];
                        return null;
                    }
                    if ("setCharacterEncoding".equals(methodName)) {
                        state.characterEncoding = (String) args[0];
                        return null;
                    }
                    if ("setStatus".equals(methodName)) {
                        state.status = (Integer) args[0];
                        return null;
                    }
                    if ("sendError".equals(methodName)) {
                        state.errorStatus = (Integer) args[0];
                        state.errorMessage = args.length > 1 ? (String) args[1] : null;
                        return null;
                    }
                    if ("getWriter".equals(methodName)) {
                        return state.writer;
                    }

                    return defaultValue(proxy, methodName, method.getReturnType(), args);
                });
    }

    private static FilterChain chainProxy(final ChainState state) {
        return (FilterChain) Proxy.newProxyInstance(
                FilterChain.class.getClassLoader(),
                new Class<?>[]{FilterChain.class},
                (proxy, method, args) -> {
                    if ("doFilter".equals(method.getName())) {
                        state.called = true;
                        return null;
                    }
                    return defaultValue(proxy, method.getName(), method.getReturnType(), args);
                });
    }

    private static Object defaultValue(final Object proxy,
                                       final String methodName,
                                       final Class<?> returnType,
                                       final Object[] args) {
        if ("toString".equals(methodName)) {
            return proxy.getClass().getName();
        }
        if ("hashCode".equals(methodName)) {
            return System.identityHashCode(proxy);
        }
        if ("equals".equals(methodName)) {
            return proxy == args[0];
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }

        return null;
    }

    private static final class RequestState {
        private final String method;
        private final String servletPath;
        private final Map<String, Object> attributes = new HashMap<>();
        private final Map<String, String> parameters = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();
        private HttpSession currentSession;
        private SessionState currentSessionState;

        private RequestState(final String method, final String servletPath) {
            this.method = method;
            this.servletPath = servletPath;
        }
    }

    private static final class SessionState {
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean invalidated;
    }

    private static final class ResponseState {
        private final Map<String, String> headers = new HashMap<>();
        private final StringWriter body = new StringWriter();
        private final PrintWriter writer = new PrintWriter(body);
        private String contentType;
        private String characterEncoding;
        private int status;
        private int errorStatus;
        private String errorMessage;
    }

    private static final class ChainState {
        private boolean called;
    }
}
