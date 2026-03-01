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
 * Handles:
 * - Registration
 * - Login
 * - Password change
 * - PIN validation
 * - Simulated 2FA
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

        validateEmail(email);
        validatePassword(password);

        if (userDAO.existsByEmail(email)) {
            throw new RuntimeException("Email already registered.");
        }

        if (userDAO.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already registered.");
        }

        String hashedPassword = PasswordUtil.hash(password);
        String hashedPin = PasswordUtil.hash(transactionPin);
        String hashedSecurityAnswer = PasswordUtil.hash(securityAnswer);

        User user = new User(
                fullName,
                email,
                phone,
                hashedPassword,
                hashedPin,
                securityQuestion,
                hashedSecurityAnswer,
                accountType
        );

        return userDAO.save(user);
    }

    /**
     * Login user
     */
    public User login(String emailOrPhone, String password) {

        Optional<User> optionalUser =
                userDAO.findByEmailOrPhone(emailOrPhone);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials.");
        }

        User user = optionalUser.get();

        if (user.getAccountStatus() == AccountStatus.LOCKED) {
            throw new RuntimeException("Account is locked.");
        }

        boolean passwordMatch =
                PasswordUtil.verify(password,
                        user.getPasswordHash());

        if (!passwordMatch) {
            userDAO.incrementFailedAttempts(user.getId());
            throw new RuntimeException("Invalid credentials.");
        }

        userDAO.resetFailedAttempts(user.getId());
        userDAO.updateLastLogin(user.getId());

        if (user.isTwoFactorEnabled()) {
            simulateTwoFactor();
        }

        return user;
    }

    /**
     * Validate transaction PIN
     */
    public void validateTransactionPin(User user,
                                       String enteredPin) {

        boolean pinMatch =
                PasswordUtil.verify(
                        enteredPin,
                        user.getTransactionPinHash());

        if (!pinMatch) {
            throw new RuntimeException(
                    "Invalid transaction PIN.");
        }
    }

    /**
     * Change password
     */
    public void changePassword(User user,
                               String currentPassword,
                               String newPassword) {

        boolean match =
                PasswordUtil.verify(
                        currentPassword,
                        user.getPasswordHash());

        if (!match) {
            throw new RuntimeException(
                    "Current password incorrect.");
        }

        validatePassword(newPassword);

        String newHashedPassword =
                PasswordUtil.hash(newPassword);

        user.updatePasswordHash(newHashedPassword);

        userDAO.update(user);
    }

    /**
     * Email validation (simple version)
     */
    private void validateEmail(String email) {

        if (email == null ||
                !email.contains("@") ||
                !email.endsWith(".com")) {

            throw new RuntimeException(
                    "Invalid email format. Must be like example@gmail.com");
        }
    }

    /**
     * Strong password validation
     * - Minimum 6 characters
     * - At least 1 letter
     * - At least 1 number
     * - At least 1 special character
     */
    private void validatePassword(String password) {

        String PASSWORD_REGEX =
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$";

        if (password == null ||
                !password.matches(PASSWORD_REGEX)) {

            throw new RuntimeException(
                    "Password must be at least 6 characters long and include " +
                            "letters, numbers, and special characters.");
        }
    }

    /**
     * Simulated 2FA
     */
    private void simulateTwoFactor() {

        int code = new Random()
                .nextInt(900000) + 100000;

        System.out.println(
                "Simulated 2FA Code: " + code);
    }
}