package it.unipd.dei.webapp.unit.util;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.util.AuthenticationSessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationSessionUtilTest {

    @Test
    void startAuthenticatedSessionInvalidatesExistingSessionAndCreatesFreshOne() {
        RequestState requestState = new RequestState();
        requestState.currentSessionState = new SessionState();
        requestState.currentSessionState.attributes.put("guestSessionToken", UUID.randomUUID());
        HttpSession existingSession = sessionProxy(requestState, requestState.currentSessionState);
        requestState.currentSession = existingSession;

        HttpServletRequest request = requestProxy(requestState);
        User user = sampleUser();

        AuthenticationSessionUtil.startAuthenticatedSession(request, user);

        assertTrue(requestState.lastInvalidatedSession.invalidated);
        assertNotSame(existingSession, requestState.currentSession);
        assertEquals(1, requestState.createdSessions);
        assertSame(user, requestState.currentSessionState.attributes.get(AuthenticationSessionUtil.AUTHENTICATED_USER_ATTRIBUTE));
        assertNull(requestState.currentSessionState.attributes.get("guestSessionToken"));
    }

    @Test
    void startAuthenticatedSessionCreatesNewSessionWhenMissing() {
        RequestState requestState = new RequestState();
        HttpServletRequest request = requestProxy(requestState);
        User user = sampleUser();

        AuthenticationSessionUtil.startAuthenticatedSession(request, user);

        assertEquals(1, requestState.createdSessions);
        assertSame(user, requestState.currentSessionState.attributes.get(AuthenticationSessionUtil.AUTHENTICATED_USER_ATTRIBUTE));
    }

    private static HttpServletRequest requestProxy(final RequestState state) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();

                    if ("getSession".equals(methodName)) {
                        boolean create = args == null || args.length == 0 || Boolean.TRUE.equals(args[0]);
                        if (state.currentSession == null && create) {
                            state.createdSessions++;
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
                    if ("removeAttribute".equals(methodName)) {
                        state.attributes.remove(args[0]);
                        return null;
                    }
                    if ("invalidate".equals(methodName)) {
                        state.invalidated = true;
                        requestState.lastInvalidatedSession = state;
                        requestState.currentSession = null;
                        requestState.currentSessionState = null;
                        return null;
                    }

                    return defaultValue(proxy, methodName, method.getReturnType(), args);
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

    private static User sampleUser() {
        return new User(
                UUID.randomUUID(),
                "Alice",
                "Tester",
                "alice@example.com",
                "hash",
                null,
                LocalDateTime.now());
    }

    private static final class RequestState {
        private HttpSession currentSession;
        private SessionState currentSessionState;
        private SessionState lastInvalidatedSession;
        private int createdSessions;
    }

    private static final class SessionState {
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean invalidated;
    }
}
