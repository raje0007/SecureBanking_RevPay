package org.revpay.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * It handles:
 * - Password hashing using bcrypt
 * - Password verification
 * - Transaction PIN hashing
 * - Security answer hashing
 *
 * Ensures secure credential storage.
 */
public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12; // Strong cost factor

    // Private constructor to prevent instantiation
    private PasswordUtil() {}

    /**
     * Hash raw password or PIN using bcrypt
     */
    public static String hash(String rawValue) {

        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("Value to hash cannot be null or empty.");
        }

        return BCrypt.hashpw(rawValue, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verify raw value against stored hash
     */
    public static boolean verify(String rawValue, String hashedValue) {

        if (rawValue == null || hashedValue == null) {
            return false;
        }

        return BCrypt.checkpw(rawValue, hashedValue);
    }
}