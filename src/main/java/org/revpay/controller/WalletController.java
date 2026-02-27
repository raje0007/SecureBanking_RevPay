package org.revpay.controller;

import org.revpay.model.User;
import org.revpay.service.WalletService;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 *
 * Handles wallet menu interaction safely with proper exception handling.
 */
public class WalletController {

    private final WalletService walletService;
    private final Scanner scanner;

    public WalletController() {
        this.walletService = new WalletService();
        this.scanner = new Scanner(System.in);
    }

    public void showWalletMenu(User user) {

        while (true) {

            System.out.println("\n==== Wallet Menu ====");
            System.out.println("1. Check Balance");
            System.out.println("2. Add Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            try {

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {

                    case 1 -> checkBalance(user);

                    case 2 -> addMoney(user);

                    case 3 -> withdrawMoney(user);

                    case 4 -> {
                        return;
                    }

                    default -> System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void checkBalance(User user) {

        try {

            BigDecimal balance =
                    walletService.getBalance(user.getId());

            System.out.println("Your current balance is: ₹ " + balance);

        } catch (Exception e) {

            System.out.println("Error fetching balance: " + e.getMessage());
        }
    }

    private void addMoney(User user) {

        try {

            System.out.print("Enter amount: ");
            BigDecimal amount =
                    new BigDecimal(scanner.nextLine());

            System.out.print("Enter transaction PIN: ");
            String pin = scanner.nextLine();

            walletService.addMoney(user.getId(), amount, pin);

            System.out.println("Money added successfully.");

        } catch (NumberFormatException e) {

            System.out.println("Invalid amount format.");

        } catch (RuntimeException e) {

            System.out.println("Error: " + e.getMessage());

        } catch (Exception e) {

            System.out.println("Unexpected error occurred.");
        }
    }

    private void withdrawMoney(User user) {

        try {

            System.out.print("Enter amount: ");
            BigDecimal amount =
                    new BigDecimal(scanner.nextLine());

            System.out.print("Enter transaction PIN: ");
            String pin = scanner.nextLine();

            walletService.withdrawMoney(user.getId(), amount, pin);

            System.out.println("Money withdrawn successfully.");

        } catch (NumberFormatException e) {

            System.out.println("Invalid amount format.");

        } catch (RuntimeException e) {

            System.out.println("Error: " + e.getMessage());

        } catch (Exception e) {

            System.out.println("Unexpected error occurred.");
        }
    }
}