package org.revpay.service;

import org.revpay.dao.UserDAO;
import org.revpay.dao.impl.UserDAOImpl;
import org.revpay.model.AccountStatus;
import org.revpay.model.AccountType;
import org.revpay.model.User;
import org.revpay.util.PasswordUtil;

import java.util.Optional;
import java.util.Random;

/**
 * AuthService is a CLASS (not interface, not annotation, not exception).
 *
 * It handles authentication and registration business logic.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Register new user (Personal or Business)
     */
    public Long register(String fullName,
                         String email,
                         String phone,
                         String password,
                         String transactionPin,
                         String securityQuestion,
                         String securityAnswer,
                         AccountType accountType) {

        if (userDAO.existsByEmail(email)) {
            throw new RuntimeException("Email already registered.");
        }

        if (userDAO.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already registered.");
        }

        String hashedPassword = PasswordUtil.hash(password);
        String hashedPin = PasswordUtil.hash(transactionPin);
        String hashedSecurityAnswer = PasswordUtil.hash(securityAnswer);

        User user = new User(fullName, email, phone,
                hashedPassword,
                hashedPin,
                securityQuestion,
                hashedSecurityAnswer,
                accountType);

        return userDAO.save(user);
    }

    /**
     * Login user with email/phone and password
     */
    public User login(String emailOrPhone, String password) {

        Optional<User> optionalUser = userDAO.findByEmailOrPhone(emailOrPhone);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials.");
        }

        User user = optionalUser.get();

        if (user.getAccountStatus() == AccountStatus.LOCKED) {
            throw new RuntimeException("Account is locked.");
        }

        boolean passwordMatch = PasswordUtil.verify(password, user.getPasswordHash());

        if (!passwordMatch) {
            userDAO.incrementFailedAttempts(user.getId());
            throw new RuntimeException("Invalid credentials.");
        }

        // Reset failed attempts on success
        userDAO.resetFailedAttempts(user.getId());
        userDAO.updateLastLogin(user.getId());

        // Simulated 2FA
        if (user.isTwoFactorEnabled()) {
            simulateTwoFactor();
        }

        return user;
    }

    /**
     * Validate transaction PIN before financial operation
     */
    public void validateTransactionPin(User user, String enteredPin) {

        boolean pinMatch = PasswordUtil.verify(enteredPin, user.getTransactionPinHash());

        if (!pinMatch) {
            throw new RuntimeException("Invalid transaction PIN.");
        }
    }

    /**
     * Change password with current password verification
     */
    public void changePassword(User user,
                               String currentPassword,
                               String newPassword) {

        boolean match = PasswordUtil.verify(currentPassword, user.getPasswordHash());

        if (!match) {
            throw new RuntimeException("Current password incorrect.");
        }

        String newHashedPassword = PasswordUtil.hash(newPassword);
        user.updatePasswordHash(newHashedPassword);

        userDAO.update(user);
    }

    /**
     * Simulated 2FA
     */
    private void simulateTwoFactor() {

        int code = new Random().nextInt(900000) + 100000;
        System.out.println("Simulated 2FA Code: " + code);

        // In real system, verify user input here
    }
}