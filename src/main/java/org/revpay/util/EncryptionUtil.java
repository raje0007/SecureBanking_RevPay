package org.revpay.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * EncryptionUtil is a UTILITY CLASS (not interface, not annotation, not exception).
 *
 * It provides AES-256 encryption and decryption
 * for securing sensitive data such as:
 * - Card numbers
 * - Bank details
 * - Confidential invoice data
 *
 * Uses AES/CBC/PKCS5Padding with random IV.
 */
public final class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    // IMPORTANT: In production, store this securely (env variable / vault)
    private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef";

    private EncryptionUtil() {}

    /**
     * Encrypt plain text using AES-256
     */
    public static String encrypt(String plainText) {

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecretKeySpec keySpec = new SecretKeySpec(
                    SECRET_KEY.getBytes(), ALGORITHM);

            byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed.", e);
        }
    }

    /**
     * Decrypt AES-256 encrypted text
     */
    public static String decrypt(String encryptedText) {

        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            byte[] iv = new byte[16];
            byte[] encryptedBytes = new byte[combined.length - 16];

            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecretKeySpec keySpec = new SecretKeySpec(
                    SECRET_KEY.getBytes(), ALGORITHM);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed.", e);
        }
    }
}