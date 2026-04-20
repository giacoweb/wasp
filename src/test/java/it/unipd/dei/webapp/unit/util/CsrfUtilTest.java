package it.unipd.dei.webapp.unit.util;

import it.unipd.dei.webapp.util.CsrfUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsrfUtilTest {

    @Test
    void generateTokenReturnsNonBlankUniqueValue() {
        String first = CsrfUtil.generateToken();
        String second = CsrfUtil.generateToken();

        assertTrue(first != null && !first.isBlank());
        assertTrue(second != null && !second.isBlank());
        assertNotEquals(first, second);
    }

    @Test
    void tokensMatchReturnsTrueForSameToken() {
        String token = CsrfUtil.generateToken();

        assertTrue(CsrfUtil.tokensMatch(token, token));
    }

    @Test
    void tokensMatchReturnsFalseForDifferentOrMissingTokens() {
        assertFalse(CsrfUtil.tokensMatch("token-a", "token-b"));
        assertFalse(CsrfUtil.tokensMatch("token-a", null));
        assertFalse(CsrfUtil.tokensMatch(null, "token-a"));
    }
}
