package org.revpay.controller;

import org.revpay.model.Loan;
import org.revpay.model.User;
import org.revpay.service.LoanService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Scanner;

public class LoanController {

    private final LoanService loanService;
    private final Scanner scanner;

    public LoanController() {
        this.loanService = new LoanService();
        this.scanner = new Scanner(System.in);
    }

    public void showLoanMenu(User user) {

        while (true) {

            System.out.println("\n==== Loan Menu ====");
            System.out.println("1. Apply Loan");
            System.out.println("2. Repay Loan");
            System.out.println("3. View Loan Details");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {

                case 1 -> applyLoan(user);

                case 2 -> repayLoan(user);

                case 3 -> viewLoan(user);

                case 4 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void applyLoan(User user) {

        try {

            System.out.print("Enter principal amount: ");
            BigDecimal principal =
                    new BigDecimal(scanner.nextLine());

            System.out.print("Enter duration (months): ");
            int months =
                    Integer.parseInt(scanner.nextLine());

            loanService.applyLoan(user, principal, months);

            System.out.println("Loan applied successfully.");

        } catch (Exception e) {
            System.out.println("Loan application failed: "
                    + e.getMessage());
        }
    }

    private void repayLoan(User user) {

        try {

            System.out.print("Enter Loan ID: ");
            Long loanId =
                    Long.parseLong(scanner.nextLine());

            System.out.print("Enter repayment amount: ");
            BigDecimal amount =
                    new BigDecimal(scanner.nextLine());

            loanService.repayLoan(user, loanId, amount);

            System.out.println("Repayment successful.");

        } catch (Exception e) {
            System.out.println("Repayment failed: "
                    + e.getMessage());
        }
    }

    private void viewLoan(User user) {

        try {

            System.out.print("Enter Loan ID: ");
            Long loanId =
                    Long.parseLong(scanner.nextLine());

            Optional<Loan> optional =
                    loanService.viewLoan(user, loanId);

            if (optional.isEmpty()) {
                System.out.println("Loan not found.");
                return;
            }

            Loan loan = optional.get();

            long daysLeft =
                    ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            loan.getDueDate()
                    );

            System.out.println("\n==== Loan Summary ====");
            System.out.println("Principal: ₹" +
                    loan.getPrincipalAmount());
            System.out.println("Interest Rate: " +
                    loan.getInterestRate() + "%");
            System.out.println("Total Payable: ₹" +
                    loan.getTotalAmount());
            System.out.println("Paid Amount: ₹" +
                    loan.getPaidAmount());
            System.out.println("Remaining Amount: ₹" +
                    loan.getRemainingAmount());
            System.out.println("Due Date: " +
                    loan.getDueDate());
            System.out.println("Days Left: " +
                    daysLeft);
            System.out.println("Status: " +
                    loan.getStatus());

        } catch (Exception e) {
            System.out.println("Error fetching loan: "
                    + e.getMessage());
        }
    }
}