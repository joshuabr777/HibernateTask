package com.gym.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.entity.User;
import com.gym.repository.UserRepository;
import com.gym.service.AuthenticationService;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user by username and password
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return Optional<User> containing the authenticated user if successful, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> authenticate(String username, String password) {
        if (isBlank(username)) {
            log.debug("Authentication failed: username is null or empty");
            return Optional.empty();
        }
        if (isBlank(password)) {
            log.debug("Authentication failed: password is null or empty");
            return Optional.empty();
        }

        try {
            String trimmedUsername = username.trim();
            log.debug("Attempting to authenticate user: {}", trimmedUsername);
            return userRepository.findByUsername(trimmedUsername)
                    .filter(user -> password.equals(user.getPassword()))
                    .filter(User::isActive)
                    .map(user -> {
                        log.info("User authenticated successfully: {}", trimmedUsername);
                        return user;
                    });
        } catch (Exception e) {
            log.error("Error during authentication for username: {}", username, e);
            return Optional.empty();
        }
    }
    
    /**
     * Validates if a user exists and is active
     * @param username the username to validate
     * @return boolean indicating if user is valid and active
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUserActiveByUsername(String username) {
        if (isBlank(username)) return false;

        try {
            return userRepository.findByUsername(username.trim())
                    .map(User::isActive)
                    .orElse(false);
        } catch (Exception e) {
            log.error("Error checking if user is active for username: {}", username, e);
            return false;
        }
    }
    
    /**
     * Changes password for an authenticated user
     * @param username the username of the user
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return boolean indicating success of password change
     */
    @Override
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (isBlank(username) || isBlank(oldPassword) || isBlank(newPassword)) {
            log.debug("Password change failed: invalid input for user {}", username);
            return false;
        }

        try {
            Optional<User> userOptional = authenticate(username, oldPassword);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(newPassword);
                userRepository.save(user);
                log.info("Password changed successfully for user: {}", username);
                return true;
            } else {
                log.warn("Password change failed: authentication failed for user: {}", username);
                return false;
            }
        } catch (Exception e) {
            log.error("Error changing password for user: {}", username, e);
            return false;
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}