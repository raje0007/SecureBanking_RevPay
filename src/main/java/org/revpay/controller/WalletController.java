package org.revpay.controller;

import org.revpay.model.User;
import org.revpay.service.WalletService;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * WalletController is a CLASS (not interface, not annotation, not exception).
 *
 * Handles wallet menu interaction.
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

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {

                case 1 -> checkBalance(user);

                case 2 -> addMoney(user);

                case 3 -> withdrawMoney(user);

                case 4 -> { return; }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void checkBalance(User user) {

        BigDecimal balance = walletService.getBalance(user.getId());
        System.out.println("Your current balance is: ₹ " + balance);
    }

    private void addMoney(User user) {

        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        System.out.print("Enter transaction PIN: ");
        String pin = scanner.nextLine();

        walletService.addMoney(user.getId(), amount, pin);

        System.out.println("Money added successfully.");
    }

    private void withdrawMoney(User user) {

        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        System.out.print("Enter transaction PIN: ");
        String pin = scanner.nextLine();

        walletService.withdrawMoney(user.getId(), amount, pin);

        System.out.println("Money withdrawn successfully.");
    }
}