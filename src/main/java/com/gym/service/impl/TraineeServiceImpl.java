package com.gym.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.User;
import com.gym.repository.TraineeRepository;
import com.gym.service.AuthenticationService;
import com.gym.service.TraineeService;
import com.gym.service.TrainingService;
import com.gym.service.UserService;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final TrainingService trainingService;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, UserService userService,
                              AuthenticationService authenticationService, @Lazy TrainingService trainingService) {
        this.traineeRepository = traineeRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.trainingService = trainingService;
    }

    /**
     * Creates a new trainee profile with a generated username and password.
     *
     * @param firstName   required first name
     * @param lastName    required last name
     * @param dateOfBirth optional date of birth
     * @param address     optional address
     * @return the created Trainee
     * @throws IllegalArgumentException if required fields are missing
     */
    @Override
    @Transactional
    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        validateRequiredFields(firstName, lastName);

        log.debug("Creating new trainee");

        User user = userService.createUser(firstName, lastName);

        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(dateOfBirth)
                .address(address != null ? address.trim() : null)
                .build();

        traineeRepository.save(trainee);
        log.info("Successfully created trainee with username: {}", user.getUsername());
        return trainee;
    }

    /**
     * Authenticates a trainee with username and password.
     *
     * @param username the trainee username
     * @param password the password
     * @return Optional<Trainee> if authentication is successful
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> authenticateTrainee(String username, String password) {
        authenticateOrThrow(username, password);
        return traineeRepository.findByUsername(username);
    }

    /**
     * Finds a trainee by username.
     *
     * @param username the trainee username
     * @return Optional<Trainee>
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username) {
        if (isBlank(username)) return Optional.empty();
        return traineeRepository.findByUsername(username.trim());
    }

    /**
     * Finds a trainee by ID.
     *
     * @param id trainee ID
     * @return Optional<Trainee>
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findById(Long id) {
        if (id == null) return Optional.empty();
        return traineeRepository.findById(id);
    }

    /**
     * Changes password for a trainee after authentication.
     *
     * @param username    the trainee username
     * @param oldPassword current password
     * @param newPassword new password
     * @return true if successful
     */
    @Override
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        authenticateOrThrow(username, oldPassword);
        return authenticationService.changePassword(username, oldPassword, newPassword);
    }

    /**
     * Updates trainee profile after authentication.
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @param trainee  updated trainee data
     * @return the updated Trainee
     */
    @Override
    @Transactional
    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        authenticateOrThrow(username, password);

        if (trainee == null || trainee.getId() == null) {
            throw new IllegalArgumentException("Trainee or Trainee ID cannot be null for update");
        }

        Trainee existingTrainee = traineeRepository.findById(trainee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with ID: " + trainee.getId()));

        if (!existingTrainee.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("Trainee does not belong to authenticated user");
        }

        if (trainee.getAddress() != null) {
            existingTrainee.setAddress(trainee.getAddress().trim());
        }

        traineeRepository.save(existingTrainee);
        log.info("Successfully updated trainee with username: {}", username);
        return existingTrainee;
    }

    /**
     * Activates a trainee profile.
     *
     * @param username trainee username
     * @param password password for authentication
     * @return true if activated successfully
     */
    @Override
    @Transactional
    public boolean activateTrainee(String username, String password) {
        return changeTraineeActiveStatus(username, password, true);
    }

    /**
     * Deactivates a trainee profile.
     *
     * @param username trainee username
     * @param password password for authentication
     * @return true if deactivated successfully
     */
    @Override
    @Transactional
    public boolean deactivateTrainee(String username, String password) {
        return changeTraineeActiveStatus(username, password, false);
    }

    /**
     * Deletes a trainee by username. This is a hard delete, cascade removes trainings.
     *
     * @param username trainee username
     * @param password password for authentication
     * @return true if deleted successfully
     */
    @Override
    @Transactional
    public boolean deleteTrainee(String username, String password) {
        authenticateOrThrow(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + username));

        traineeRepository.delete(trainee);
        userService.deleteUser(trainee.getUser());
        log.info("Successfully deleted trainee with username: {}", username);
        return true;
    }

    /**
     * Returns trainee trainings filtered by criteria.
     *
     * @param username         trainee username
     * @param password         password for authentication
     * @param fromDate         optional filter
     * @param toDate           optional filter
     * @param trainerName      optional filter
     * @param trainingTypeName optional filter
     * @return list of Trainings
     */
    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String username, String password,
                                               LocalDate fromDate, LocalDate toDate,
                                               String trainerName, String trainingTypeName) {
        authenticateOrThrow(username, password);
        return trainingService.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    /**
     * Returns unassigned trainers for a trainee.
     *
     * @param username trainee username
     * @param password password for authentication
     * @return list of Trainers
     */
    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String username, String password) {
        authenticateOrThrow(username, password);
        return traineeRepository.findUnassignedTrainers(username);
    }

    /**
     * Updates trainee's trainer list.
     *
     * @param username trainee username
     * @param password password for authentication
     * @param trainers set of trainers to assign
     * @return true if updated successfully
     */
    @Override
    @Transactional
    public boolean updateTraineeTrainers(String username, String password, Set<Trainer> trainers) {
        authenticateOrThrow(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + username));

        trainee.getTrainers().clear();
        if (trainers != null) trainee.getTrainers().addAll(trainers);

        traineeRepository.save(trainee);
        log.info("Successfully updated trainers list for trainee: {}", username);
        return true;
    }

    private User authenticateOrThrow(String username, String password) {
        return authenticationService.authenticate(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Authentication failed for username: " + username));
    }

    private boolean changeTraineeActiveStatus(String username, String password, boolean isActive) {
        authenticateOrThrow(username, password);
        return isActive ? userService.activateUser(username) : userService.deactivateUser(username);
    }

    private void validateRequiredFields(String firstName, String lastName) {
        if (isBlank(firstName)) throw new IllegalArgumentException("First name cannot be null or empty");
        if (isBlank(lastName)) throw new IllegalArgumentException("Last name cannot be null or empty");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}