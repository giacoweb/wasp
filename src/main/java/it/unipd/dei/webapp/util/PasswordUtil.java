package it.unipd.dei.webapp.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Utility class
    }

    public static String hashPassword(final String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }

        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verifyPassword(final String plainPassword, final String hashedPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }

        if (hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }

        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}