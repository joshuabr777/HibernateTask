package com.gym.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;
import com.gym.entity.User;
import com.gym.repository.TrainerRepository;
import com.gym.service.AuthenticationService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import com.gym.service.TrainingTypeService;
import com.gym.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;

    private final UserService userService;

    private final AuthenticationService authenticationService;

    private final TrainingService trainingService;

    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository, UserService userService,
                               AuthenticationService authenticationService, TrainingService trainingService,
                               TrainingTypeService trainingTypeService) {
        this.trainerRepository = trainerRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }

    /**
     * Changes password for a trainer (requires authentication)
     * @param username the username
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return boolean indicating success
     */
    @Override
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = authenticateOrThrow(username, oldPassword);
        return authenticationService.changePassword(user.getUsername(), oldPassword, newPassword);
    }
    
    /**
     * Updates trainer profile (requires authentication)
     * @param username the username for authentication
     * @param password the password for authentication
     * @param trainer the updated trainer data
     * @return the updated trainer
     * @throws IllegalArgumentException if authentication fails or data is invalid
     */
    @Override
    @Transactional
    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        User authUser = authenticateOrThrow(username, password);

        validateTrainerForUpdate(trainer);

        log.debug("Updating trainer for user ID: {}", authUser.getId());

        Trainer existingTrainer = trainerRepository.findById(trainer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + trainer.getId()));

        if (!existingTrainer.getUser().getId().equals(authUser.getId())) {
            throw new IllegalArgumentException("Trainer does not belong to authenticated user");
        }

        User updatedUser = trainer.getUser();
        if (updatedUser != null) {
            // Keep original identity values
            updatedUser.setId(authUser.getId());
            updatedUser.setUsername(authUser.getUsername());
            updatedUser.setPassword(authUser.getPassword());
            userService.updateUser(updatedUser);
        }

        trainerRepository.save(trainer);
        log.info("Trainer updated successfully for user ID: {}", authUser.getId());
        return trainer;
    }
    
    /**
     * Activates a trainer (requires authentication)
     * @param username the username for authentication
     * @param password the password for authentication
     * @return boolean indicating success
     */
    @Override
    @Transactional
    public boolean activateTrainer(String username, String password) {
        return changeTrainerActiveStatus(username, password, true);
    }
    
    /**
     * Deactivates a trainer (requires authentication)
     * @param username the username for authentication
     * @param password the password for authentication
     * @return boolean indicating success
     */
    @Override
    @Transactional
    public boolean deactivateTrainer(String username, String password) {
        return changeTrainerActiveStatus(username, password, false);
    }
    
    
    /**
     * Gets the list of trainings for a specific trainer (requires authentication)
     * @param username the username for authentication
     * @param password the password for authentication
     * @param fromDate the start date for filtering trainings
     * @param toDate the end date for filtering trainings
     * @param traineeName the name of the trainee (optional)
     * @return the list of trainings for the trainer
     */
    @Override
    public List<Training> getTrainerTrainings(String username, String password,
                                              LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        User user = authenticateOrThrow(username, password);
        log.debug("Fetching trainings for trainer ID: {}", user.getId());
        return trainingService.findTrainerTrainings(user.getUsername(), fromDate, toDate, traineeName);
    }

    /**
     * Creates a new trainer (requires authentication)
     * @param username the username for authentication
     * @param password the password for authentication
     * @param firstName the first name of the trainer
     * @param lastName the last name of the trainer
     * @param specialization the specialization of the trainer
     * @return the created trainer
     */
    @Override
    @Transactional
    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        validateName(firstName, "First name");
        validateName(lastName, "Last name");
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }

        log.debug("Creating new trainer with specialization ID: {}", specialization.getId());

        User user = userService.createUser(firstName, lastName);

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();

        trainerRepository.save(trainer);
        log.info("Trainer created successfully with username: {}", user.getUsername());
        return trainer;
    }

    /**
     * Creates a new trainer (requires authentication)
     * @param username the username for authentication
     * @param password the password for authentication
     * @param firstName the first name of the trainer
     * @param lastName the last name of the trainer
     * @param specialization the specialization of the trainer
     * @return the created trainer
     */
    @Override
    @Transactional
    public Trainer createTrainer(String firstName, String lastName, String specializationName) {
        validateName(firstName, "First name");
        validateName(lastName, "Last name");
        validateName(specializationName, "Specialization name");

        TrainingType specialization = resolveSpecialization(specializationName);
        return createTrainer(firstName, lastName, specialization);
    }

    /**
     * Authenticates a trainer (requires authentication)
     */
    @Override
    public Optional<Trainer> authenticateTrainer(String username, String password) {
        Optional<User> userOptional = authenticationService.authenticate(username, password);
        return userOptional.flatMap(u -> trainerRepository.findByUsername(username));
    }

    /**
     * Finds a trainer by username (requires authentication)
     */
    @Override
    public Optional<Trainer> findByUsername(String username) {
        if (isBlank(username)) {
            return Optional.empty();
        }
        return trainerRepository.findByUsername(username.trim());
    }

    /**
     * Finds a trainer by ID (requires authentication)
     */
    @Override
    public Optional<Trainer> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return trainerRepository.findById(id);
    }

    private User authenticateOrThrow(String username, String password) {
        return authenticationService.authenticate(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Authentication failed"));
    }

    private boolean changeTrainerActiveStatus(String username, String password, boolean isActive) {
        User user = authenticateOrThrow(username, password);
        log.debug("Changing trainer active status for user ID: {} to {}", user.getId(), isActive);
        return isActive ? userService.activateUser(username) : userService.deactivateUser(username);
    }

    private void validateTrainerForUpdate(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        if (trainer.getId() == null) {
            throw new IllegalArgumentException("Trainer ID cannot be null for update");
        }
        if (trainer.getSpecialization() == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }
    }

    private void validateName(String value, String fieldName) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private TrainingType resolveSpecialization(String specializationName) {
        return trainingTypeService.findByName(specializationName.trim())
                .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + specializationName));
    }
}