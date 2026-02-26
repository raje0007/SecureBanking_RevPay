package org.revpay.service;

import org.revpay.config.DBConnection;
import org.revpay.dao.UserDAO;
import org.revpay.dao.impl.UserDAOImpl;
import org.revpay.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * WalletService is a CLASS (not interface, not annotation, not exception).
 *
 * Handles wallet-related business logic:
 * - Add money
 * - Withdraw money
 * - Check balance
 * - Send notifications
 */
public class WalletService {

    private final UserDAO userDAO;
    private final AuthService authService;
    private final NotificationService notificationService;

    private static final BigDecimal LOW_BALANCE_THRESHOLD =
            new BigDecimal("1000");

    public WalletService() {
        this.userDAO = new UserDAOImpl();
        this.authService = new AuthService();
        this.notificationService = new NotificationService();
    }

    /**
     * Add money to wallet
     */
    public void addMoney(Long userId,
                         BigDecimal amount,
                         String transactionPin) {

        validateAmount(amount);

        User user = getUserOrThrow(userId);

        authService.validateTransactionPin(user, transactionPin);

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String sql =
                        "UPDATE users SET balance = balance + ? WHERE id = ?";

                try (PreparedStatement ps =
                             connection.prepareStatement(sql)) {

                    ps.setBigDecimal(1, amount);
                    ps.setLong(2, userId);
                    ps.executeUpdate();
                }

                connection.commit();

                notificationService.sendNotification(
                        userId,
                        "Wallet Credited",
                        "₹" + amount +
                                " has been added to your wallet.",
                        "WALLET"
                );

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to add money.", e);
        }
    }

    /**
     * Withdraw money from wallet
     */
    public void withdrawMoney(Long userId,
                              BigDecimal amount,
                              String transactionPin) {

        validateAmount(amount);

        User user = getUserOrThrow(userId);

        authService.validateTransactionPin(user, transactionPin);

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String sql =
                        "UPDATE users SET balance = balance - ? " +
                                "WHERE id = ? AND balance >= ?";

                int affectedRows;

                try (PreparedStatement ps =
                             connection.prepareStatement(sql)) {

                    ps.setBigDecimal(1, amount);
                    ps.setLong(2, userId);
                    ps.setBigDecimal(3, amount);

                    affectedRows = ps.executeUpdate();
                }

                if (affectedRows == 0) {
                    throw new RuntimeException(
                            "Insufficient balance.");
                }

                connection.commit();

                notificationService.sendNotification(
                        userId,
                        "Wallet Debited",
                        "₹" + amount +
                                " has been deducted from your wallet.",
                        "WALLET"
                );

                BigDecimal newBalance = getBalance(userId);

                if (newBalance.compareTo(
                        LOW_BALANCE_THRESHOLD) < 0) {

                    notificationService.sendNotification(
                            userId,
                            "Low Balance Alert",
                            "Your wallet balance is below ₹1000.",
                            "ALERT"
                    );
                }

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to withdraw money.", e);
        }
    }

    /**
     * Get current wallet balance
     */
    public BigDecimal getBalance(Long userId) {

        String sql =
                "SELECT balance FROM users WHERE id = ?";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to fetch balance.", e);
        }

        throw new RuntimeException("User not found.");
    }

    // =========================
    // Helper Methods
    // =========================

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero.");
        }
    }

    private User getUserOrThrow(Long userId) {

        Optional<User> optional =
                userDAO.findById(userId);

        if (optional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        return optional.get();
    }
}