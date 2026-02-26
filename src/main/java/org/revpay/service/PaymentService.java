package org.revpay.service;

import org.revpay.config.DBConnection;
import org.revpay.dao.TransactionDAO;
import org.revpay.dao.UserDAO;
import org.revpay.dao.impl.TransactionDAOImpl;
import org.revpay.dao.impl.UserDAOImpl;
import org.revpay.model.Transaction;
import org.revpay.model.TransactionStatus;
import org.revpay.model.TransactionType;
import org.revpay.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * PaymentService is a CLASS (not interface, not annotation, not exception).
 *
 * It handles financial transaction business logic such as:
 * - Sending money
 * - Updating user balances
 * - Creating transaction records
 * - Managing atomic database transactions
 */
public class PaymentService {

    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    private final AuthService authService;

    public PaymentService() {
        this.userDAO = new UserDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
        this.authService = new AuthService();
    }

    /**
     * Send money between users (atomic operation)
     */
    public void sendMoney(Long senderId,
                          String receiverIdentifier,
                          BigDecimal amount,
                          String note,
                          String transactionPin) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero.");
        }

        Optional<User> senderOpt = userDAO.findById(senderId);
        if (senderOpt.isEmpty()) {
            throw new RuntimeException("Sender not found.");
        }

        User sender = senderOpt.get();

        // Validate Transaction PIN
        authService.validateTransactionPin(sender, transactionPin);

        Optional<User> receiverOpt = userDAO.findByEmailOrPhone(receiverIdentifier);
        if (receiverOpt.isEmpty()) {
            throw new RuntimeException("Receiver not found.");
        }

        User receiver = receiverOpt.get();

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Cannot send money to yourself.");
        }

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Deduct from sender (prevent overdraft)
                String deductSql = "UPDATE users SET balance = balance - ? " +
                        "WHERE id = ? AND balance >= ?";

                try (PreparedStatement deductStmt = connection.prepareStatement(deductSql)) {
                    deductStmt.setBigDecimal(1, amount);
                    deductStmt.setLong(2, sender.getId());
                    deductStmt.setBigDecimal(3, amount);

                    int affectedRows = deductStmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new RuntimeException("Insufficient balance.");
                    }
                }

                // Add to receiver
                String creditSql = "UPDATE users SET balance = balance + ? WHERE id = ?";

                try (PreparedStatement creditStmt = connection.prepareStatement(creditSql)) {
                    creditStmt.setBigDecimal(1, amount);
                    creditStmt.setLong(2, receiver.getId());
                    creditStmt.executeUpdate();
                }

                // Create transaction record
                Transaction transaction = new Transaction(
                        sender.getId(),
                        receiver.getId(),
                        amount,
                        TransactionType.SEND_MONEY,
                        note
                );

                transaction.markCompleted();

                transactionDAO.save(transaction);

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Payment processing failed.", e);
        }
    }
}