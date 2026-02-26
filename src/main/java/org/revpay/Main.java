package org.revpay;

import org.revpay.controller.AuthController;
import org.revpay.controller.InvoiceController;
import org.revpay.controller.LoanController;
import org.revpay.controller.PaymentController;
import org.revpay.controller.WalletController;
import org.revpay.model.User;

import java.util.Scanner;

/**
 *
 * Entry point of RevPay application.
 * Integrates:
 * - Authentication
 * - Wallet
 * - Payment
 * - Invoice
 * - Loan
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Welcome to RevPay");
        System.out.println("   Secure Digital Payment System");
        System.out.println("=================================");

        AuthController authController = new AuthController();

        while (true) {

            User loggedInUser = authController.authenticateUser();

            if (loggedInUser == null) {
                System.out.println("Exiting application...");
                break;
            }

            showDashboard(loggedInUser);
        }
    }

    /**
     * Dashboard after successful login
     */
    private static void showDashboard(User user) {

        WalletController walletController = new WalletController();
        PaymentController paymentController = new PaymentController();
        InvoiceController invoiceController = new InvoiceController();
        LoanController loanController = new LoanController();

        while (true) {

            System.out.println("\n==== Main Dashboard ====");
            System.out.println("Logged in as: " + user.getFullName());
            System.out.println("Account Type: " + user.getAccountType());
            System.out.println("---------------------------------");
            System.out.println("1. Wallet");
            System.out.println("2. Send Money");
            System.out.println("3. Loans");
            System.out.println("4. Invoices (Business Only)");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {

                case 1 -> walletController.showWalletMenu(user);

                case 2 -> paymentController.showPaymentMenu(user);

                case 3 -> {

                    // If loan only for business users
                    if (user.isBusinessAccount()) {
                        loanController.showLoanMenu(user);
                    } else {
                        System.out.println("Loan feature is available for business accounts only.");
                    }

                    // If you later allow personal loans,
                    // just remove the if condition above.
                }

                case 4 -> {
                    if (user.isBusinessAccount()) {
                        invoiceController.showInvoiceMenu(user);
                    } else {
                        System.out.println("Access denied. Only business accounts can access invoices.");
                    }
                }

                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }
}