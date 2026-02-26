package org.revpay.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * It handles in-app notification formatting and display.
 * Designed to support:
 * - Transaction alerts
 * - Invoice notifications
 * - Payment confirmations
 * - Low balance alerts
 * - Security alerts
 *
 * In future, this can be extended to:
 * - Database persistence
 * - Email/SMS integration
 * - Push notification system
 */
public final class NotificationUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor to prevent instantiation
    private NotificationUtil() {}

    /**
     * Send generic notification
     */
    public static void notify(String category, String message) {

        String timestamp = LocalDateTime.now().format(FORMATTER);

        System.out.println("\n========== NOTIFICATION ==========");
        System.out.println("Category : " + category);
        System.out.println("Time     : " + timestamp);
        System.out.println("Message  : " + message);
        System.out.println("==================================\n");
    }

    /**
     * Transaction notification
     */
    public static void transactionAlert(String message) {
        notify("TRANSACTION", message);
    }

    /**
     * Invoice notification
     */
    public static void invoiceAlert(String message) {
        notify("INVOICE", message);
    }

    /**
     * Security notification
     */
    public static void securityAlert(String message) {
        notify("SECURITY", message);
    }

    /**
     * Low balance alert
     */
    public static void lowBalanceAlert(String message) {
        notify("LOW_BALANCE", message);
    }
}