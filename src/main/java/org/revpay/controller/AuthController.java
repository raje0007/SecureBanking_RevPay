package org.revpay.controller;

import org.revpay.model.AccountType;
import org.revpay.model.User;
import org.revpay.service.AuthService;

import java.util.Scanner;

/**
 * It handles user interaction related to:
 * - Registration
 * - Login
 * - Authentication flow
 */
public class AuthController {

    private final AuthService authService;
    private final Scanner scanner;

    public AuthController() {
        this.authService = new AuthService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Authentication menu.
     * Returns logged-in User object.
     * Returns null if user chooses Exit.
     */
    public User authenticateUser() {

        while (true) {
            System.out.println("\n==== RevPay Authentication ====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> handleRegistration();

                case 2 -> {
                    User user = handleLogin();
                    if (user != null) {
                        return user;  // Return authenticated user
                    }
                }

                case 3 -> {
                    System.out.println("Exiting application...");
                    return null;
                }

                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Handle user registration
     */
    private void handleRegistration() {

        try {
            System.out.print("Full Name: ");
            String fullName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Transaction PIN: ");
            String pin = scanner.nextLine();

            System.out.print("Security Question: ");
            String securityQuestion = scanner.nextLine();

            System.out.print("Security Answer: ");
            String securityAnswer = scanner.nextLine();

            System.out.print("Account Type (1 = Personal, 2 = Business): ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            AccountType accountType =
                    (typeChoice == 2) ? AccountType.BUSINESS : AccountType.PERSONAL;

            Long userId = authService.register(
                    fullName,
                    email,
                    phone,
                    password,
                    pin,
                    securityQuestion,
                    securityAnswer,
                    accountType
            );

            System.out.println("Registration successful! User ID: " + userId);

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Handle login and return authenticated user
     */
    private User handleLogin() {

        try {
            System.out.print("Email or Phone: ");
            String emailOrPhone = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(emailOrPhone, password);

            System.out.println("Login successful! Welcome, " + user.getFullName());
            return user;

        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }
}