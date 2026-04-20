package it.unipd.dei.webapp.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionCookieHardeningFilterTest {

    @Test
    void hardenSessionCookieHeaderAddsHttpOnlyAndSameSiteOnInsecureRequests() {
        String original = "JSESSIONID=abc123; Path=/; Max-Age=1800";

        String hardened = SessionCookieHardeningFilter.hardenSessionCookieHeader(original, false);

        assertTrue(hardened.contains("HttpOnly"));
        assertTrue(hardened.contains("SameSite=Lax"));
        assertTrue(!hardened.contains("Secure"));
    }

    @Test
    void hardenSessionCookieHeaderAddsSecureOnSecureRequests() {
        String original = "JSESSIONID=abc123; Path=/";

        String hardened = SessionCookieHardeningFilter.hardenSessionCookieHeader(original, true);

        assertTrue(hardened.contains("HttpOnly"));
        assertTrue(hardened.contains("SameSite=Lax"));
        assertTrue(hardened.contains("Secure"));
    }

    @Test
    void hardenSessionCookieHeaderDoesNotDuplicateExistingAttributes() {
        String original = "JSESSIONID=abc123; Path=/; HttpOnly; SameSite=Lax; Secure";

        String hardened = SessionCookieHardeningFilter.hardenSessionCookieHeader(original, true);

        assertEquals(original, hardened);
    }

    @Test
    void hardenSessionCookieHeaderLeavesNonSessionCookiesUnchanged() {
        String original = "theme=light; Path=/";

        String hardened = SessionCookieHardeningFilter.hardenSessionCookieHeader(original, true);

        assertEquals(original, hardened);
    }
}
