package it.unipd.dei.webapp.unit.util;

import it.unipd.dei.webapp.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashPasswordReturnsNonNullResult() {
        String hash = PasswordUtil.hashPassword("secret123");
        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }

    @Test
    void hashPasswordProducesExpectedFormat() {
        String hash = PasswordUtil.hashPassword("secret123");
        String[] parts = hash.split(":");
        assertEquals(3, parts.length);
    }

    @Test
    void hashPasswordProducesDifferentHashesForSameInput() {
        String hash1 = PasswordUtil.hashPassword("secret123");
        String hash2 = PasswordUtil.hashPassword("secret123");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void verifyPasswordReturnsTrueForCorrectPassword() {
        String hash = PasswordUtil.hashPassword("secret123");
        assertTrue(PasswordUtil.verifyPassword("secret123", hash));
    }

    @Test
    void verifyPasswordReturnsFalseForWrongPassword() {
        String hash = PasswordUtil.hashPassword("secret123");
        assertFalse(PasswordUtil.verifyPassword("wrongpassword", hash));
    }

    @Test
    void verifyPasswordReturnsFalseForNullHash() {
        assertFalse(PasswordUtil.verifyPassword("secret123", null));
    }

    @Test
    void verifyPasswordReturnsFalseForBlankHash() {
        assertFalse(PasswordUtil.verifyPassword("secret123", ""));
    }

    @Test
    void verifyPasswordReturnsFalseForMalformedHash() {
        assertFalse(PasswordUtil.verifyPassword("secret123", "not:a:valid:hash"));
    }

    @Test
    void hashPasswordThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
    }

    @Test
    void hashPasswordThrowsOnBlank() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
    }

    @Test
    void verifyPasswordThrowsOnNullPlainPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.verifyPassword(null, "somehash"));
    }

    @Test
    void verifyPasswordThrowsOnBlankPlainPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.verifyPassword("", "somehash"));
    }
}
