package com.gym.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.entity.User;
import com.gym.repository.UserRepository;
import com.gym.service.UserService;
import com.gym.util.Helpers;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user with generated username and password
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @return the created user
     * @throws IllegalArgumentException if required fields are null or empty
     */
    @Transactional
    @Override
    public User createUser(String firstName, String lastName) {
        validateRequiredFields(firstName, lastName);

        log.debug("Creating new user record");

        // Get all existing usernames for username generation
        List<String> existingUsernames = userRepository.findAll()
                .stream()
                .map(User::getUsername)
                .toList();

        String username = Helpers.generateUsername(firstName, lastName, existingUsernames);
        String password = Helpers.generatePassword();

        User user = User.builder()
                .firstName(firstName.trim())
                .lastName(lastName.trim())
                .username(username)
                .password(password)
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("Successfully created user with username: {}", username);
        return user;
    }

    /**
     * Finds a user by ID
     * @param id the user ID
     * @return Optional<User>
     */
    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            log.debug("User ID is null");
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    /**
     * Finds a user by username
     * @param username the username
     * @return Optional<User>
     */
    @Override
    public Optional<User> findByUsername(String username) {
        if (isBlank(username)) {
            log.debug("Username is null or empty");
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    /**
     * Updates user information
     * @param user the user to update
     * @return the updated user
     * @throws IllegalArgumentException if user is null or has invalid data
     */
    @Transactional
    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }

        validateRequiredFields(user.getFirstName(), user.getLastName());

        if (isBlank(user.getUsername())) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (isBlank(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        log.debug("Updating user with ID: {}", user.getId());

        // Verify user exists
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + user.getId());
        }

        // Trim fields
        user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());

        userRepository.save(user);
        log.info("Successfully updated user with ID: {}", user.getId());
        return user;
    }

    /**
     * Activates a user
     */
    @Override
    @Transactional
    public boolean activateUser(String username) {
        return changeUserActiveStatus(username, true);
    }

    /**
     * Deactivates a user
     */
    @Override
    @Transactional
    public boolean deactivateUser(String username) {
        return changeUserActiveStatus(username, false);
    }

    /**
     * Deletes a user
     * @param user the user to delete
     * @throws IllegalArgumentException if user is null
     */
    @Transactional
    @Override
    public void deleteUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        log.debug("Deleting user with ID: {}", user.getId());
        userRepository.delete(user);
        log.info("Successfully deleted user with ID: {}", user.getId());
    }

    /**
     * Gets all users
     * @return List of all users
     */
    @Override
    public List<User> findAll() {
        log.debug("Finding all users");
        return userRepository.findAll();
    }

    // Private helper methods

    private void validateRequiredFields(String firstName, String lastName) {
        if (isBlank(firstName)) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (isBlank(lastName)) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
    }

    private boolean changeUserActiveStatus(String username, boolean isActive) {
        if (isBlank(username)) {
            log.debug("Username is null or empty for status change");
            return false;
        }

        Optional<User> userOptional = userRepository.findByUsername(username.trim());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isActive() == isActive) {
                log.debug("User {} already has requested status: {}", username, isActive);
                return false;
            }

            user.setActive(isActive);
            userRepository.save(user);
            log.info("Successfully {} user: {}", isActive ? "activated" : "deactivated", username);
            return true;
        } else {
            log.warn("User not found for status change: {}", username);
            return false;
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
