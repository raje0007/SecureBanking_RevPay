package org.revpay.dao;

import org.revpay.model.User;
import org.revpay.model.AccountStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entity.
 *
 * This is an INTERFACE (not class, not annotation, not exception).
 * It defines database operations related to User management.
 */
public interface UserDAO {

    /**
     * Save a new user into the database.
     *
     * @param user User object
     * @return generated user ID
     */
    Long save(User user);

    /**
     * Find user by ID.
     *
     * @param id user ID
     * @return Optional<User>
     */
    Optional<User> findById(Long id);

    /**
     * Find user by email.
     *
     * @param email user email
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by phone number.
     *
     * @param phoneNumber user phone
     * @return Optional<User>
     */
    Optional<User> findByPhone(String phoneNumber);

    /**
     * Find user by email or phone (used during login).
     *
     * @param emailOrPhone input credential
     * @return Optional<User>
     */
    Optional<User> findByEmailOrPhone(String emailOrPhone);

    /**
     * Update user information.
     *
     * @param user updated user object
     */
    void update(User user);

    /**
     * Update account status (ACTIVE, LOCKED, SUSPENDED).
     *
     * @param userId user ID
     * @param status new account status
     */
    void updateAccountStatus(Long userId, AccountStatus status);

    /**
     * Increment failed login attempts.
     *
     * @param userId user ID
     */
    void incrementFailedAttempts(Long userId);

    /**
     * Reset failed login attempts after successful login.
     *
     * @param userId user ID
     */
    void resetFailedAttempts(Long userId);

    /**
     * Update last login timestamp.
     *
     * @param userId user ID
     */
    void updateLastLogin(Long userId);

    /**
     * Check if email already exists.
     *
     * @param email user email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone already exists.
     *
     * @param phoneNumber user phone
     * @return true if exists
     */
    boolean existsByPhone(String phoneNumber);

    /**
     * Retrieve all users (admin use / analytics).
     *
     * @return list of users
     */
    List<User> findAll();
}