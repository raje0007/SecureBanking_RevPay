package org.revpay.service;

import org.revpay.config.DBConnection;
import org.revpay.dao.LoanDAO;
import org.revpay.dao.UserDAO;
import org.revpay.dao.impl.LoanDAOImpl;
import org.revpay.dao.impl.UserDAOImpl;
import org.revpay.model.Loan;
import org.revpay.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * LoanService is a CLASS (not interface, not annotation, not exception).
 *
 * Handles:
 * - Loan application
 * - Loan repayment
 * - Loan viewing
 * - Business rules validation
 */
public class LoanService {

    private final LoanDAO loanDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    private static final BigDecimal MIN_BALANCE =
            new BigDecimal("50000");

    private static final BigDecimal BASE_INTEREST =
            new BigDecimal("8");

    private static final BigDecimal EXTENDED_INTEREST =
            new BigDecimal("10");

    public LoanService() {
        this.loanDAO = new LoanDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.notificationService = new NotificationService();
    }

    /**
     * Apply for loan
     */
    public void applyLoan(User user,
                          BigDecimal principal,
                          int durationMonths) {

        validatePrincipal(principal);

        if (user.getBalance().compareTo(MIN_BALANCE) < 0) {
            throw new RuntimeException(
                    "Minimum ₹50,000 balance required.");
        }

        BigDecimal interestRate =
                durationMonths > 12
                        ? EXTENDED_INTEREST
                        : BASE_INTEREST;

        BigDecimal interest =
                principal.multiply(interestRate)
                        .divide(new BigDecimal("100"));

        BigDecimal totalAmount =
                principal.add(interest);

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                Loan loan = new Loan();
                loan.setUserId(user.getId());
                loan.setPrincipalAmount(principal);
                loan.setInterestRate(interestRate);
                loan.setTotalAmount(totalAmount);
                loan.setPaidAmount(BigDecimal.ZERO);
                loan.setRemainingAmount(totalAmount);
                loan.setStartDate(LocalDate.now());
                loan.setDueDate(
                        LocalDate.now()
                                .plusMonths(durationMonths)
                );
                loan.setStatus("ACTIVE");

                loanDAO.save(loan);

                // Credit principal to wallet
                user.setBalance(
                        user.getBalance().add(principal)
                );

                userDAO.update(user);

                connection.commit();

                notificationService.sendNotification(
                        user.getId(),
                        "Loan Approved",
                        "Loan of ₹" + principal +
                                " approved at " +
                                interestRate + "% interest.",
                        "LOAN"
                );

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to apply loan.", e);
        }
    }

    /**
     * Repay loan
     */
    public void repayLoan(User user,
                          Long loanId,
                          BigDecimal amount) {

        validatePrincipal(amount);

        Optional<Loan> optional =
                loanDAO.findByIdAndUser(
                        loanId,
                        user.getId()
                );

        if (optional.isEmpty()) {
            throw new RuntimeException(
                    "Loan not found.");
        }

        Loan loan = optional.get();

        if (user.getBalance()
                .compareTo(amount) < 0) {

            throw new RuntimeException(
                    "Insufficient wallet balance.");
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                BigDecimal newPaid =
                        loan.getPaidAmount().add(amount);

                BigDecimal newRemaining =
                        loan.getRemainingAmount()
                                .subtract(amount);

                if (newRemaining.compareTo(
                        BigDecimal.ZERO) < 0) {
                    throw new RuntimeException(
                            "Repayment exceeds remaining amount.");
                }

                String status =
                        newRemaining.compareTo(
                                BigDecimal.ZERO) == 0
                                ? "COMPLETED"
                                : "ACTIVE";

                loanDAO.updateLoanAmounts(
                        loanId,
                        newPaid,
                        newRemaining,
                        status
                );

                user.setBalance(
                        user.getBalance().subtract(amount)
                );

                userDAO.update(user);

                connection.commit();

                notificationService.sendNotification(
                        user.getId(),
                        "Loan Repayment Successful",
                        "₹" + amount +
                                " repaid. Remaining: ₹" +
                                newRemaining,
                        "LOAN"
                );

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to repay loan.", e);
        }
    }

    /**
     * View loan
     */
    public Optional<Loan> viewLoan(User user,
                                   Long loanId) {

        Optional<Loan> optional =
                loanDAO.findByIdAndUser(
                        loanId,
                        user.getId()
                );

        optional.ifPresent(loan -> {

            if (LocalDate.now()
                    .isAfter(loan.getDueDate())
                    && !"COMPLETED"
                    .equals(loan.getStatus())) {

                loan.setStatus("OVERDUE");
            }

            long daysLeft =
                    ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            loan.getDueDate()
                    );

            // You can print this in controller
        });

        return optional;
    }

    // =========================
    // Helper
    // =========================

    private void validatePrincipal(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero.");
        }
    }
}