package org.revpay.controller;

import org.revpay.dao.TransactionDAO;
import org.revpay.dao.impl.TransactionDAOImpl;
import org.revpay.model.Transaction;
import org.revpay.model.User;
import org.revpay.service.PaymentService;
import org.revpay.service.WalletService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * It handles user interaction for:
 * - Sending money
 * - Viewing transaction history
 * - Checking wallet balance
 */
public class PaymentController {

    private final PaymentService paymentService;
    private final WalletService walletService;
    private final TransactionDAO transactionDAO;
    private final Scanner scanner;

    public PaymentController() {
        this.paymentService = new PaymentService();
        this.walletService = new WalletService();
        this.transactionDAO = new TransactionDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    /*
     * Show payment dashboard
     */
    public void showPaymentMenu(User loggedInUser) {

        while (true) {
            System.out.println("\n==== RevPay Payment Dashboard ====");
            System.out.println("1. Send Money");
            System.out.println("2. View Transaction History");
            System.out.println("3. Check Wallet Balance");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> handleSendMoney(loggedInUser);
                case 2 -> handleViewTransactions(loggedInUser);
                case 3 -> handleCheckBalance(loggedInUser);
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /*
     * Handle send money flow
     */
    private void handleSendMoney(User sender) {

        try {
            System.out.print("Enter receiver Email/Phone: ");
            String receiverIdentifier = scanner.nextLine();

            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Enter note (optional): ");
            String note = scanner.nextLine();

            System.out.print("Enter Transaction PIN: ");
            String pin = scanner.nextLine();

            paymentService.sendMoney(
                    sender.getId(),
                    receiverIdentifier,
                    amount,
                    note,
                    pin
            );

            System.out.println("Money sent successfully!");

        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }

    /*
     * Handle transaction history
     */
    private void handleViewTransactions(User user) {

        try {
            List<Transaction> transactions =
                    transactionDAO.findByUserId(user.getId());

            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println("\n==== Transaction History ====");

            for (Transaction txn : transactions) {
                System.out.println("----------------------------------");
                System.out.println("Reference: " + txn.getTransactionReference());
                System.out.println("Amount: " + txn.getAmount());
                System.out.println("Type: " + txn.getTransactionType());
                System.out.println("Status: " + txn.getTransactionStatus());
                System.out.println("Date: " + txn.getCreatedAt());
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch transactions: " + e.getMessage());
        }
    }

    /**
     * Handle balance check
     */
    private void handleCheckBalance(User user) {

        try {
            BigDecimal balance = walletService.getBalance(user.getId());
            System.out.println("Current Wallet Balance: " + balance);

        } catch (Exception e) {
            System.out.println("Unable to fetch balance: " + e.getMessage());
        }
    }
}