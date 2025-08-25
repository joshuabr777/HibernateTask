package com.gym.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;
import com.gym.entity.User;
import com.gym.repository.TrainingRepository;
import com.gym.service.AuthenticationService;
import com.gym.service.TraineeService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import com.gym.service.TrainingTypeService;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final AuthenticationService authenticationService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, AuthenticationService authenticationService,
                               TraineeService traineeService, TrainerService trainerService,
                               TrainingTypeService trainingTypeService) {
        this.trainingRepository = trainingRepository;
        this.authenticationService = authenticationService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Training addTraining(String traineeUsername, String trainerUsername, String trainingName,
                                Long trainingTypeId, LocalDate trainingDate, Integer duration,
                                String authenticatedUsername, String password) {
        authenticateOrThrow(authenticatedUsername, password);
        validateTrainingFields(traineeUsername, trainerUsername, trainingName, trainingTypeId, trainingDate, duration);

        try {
            Trainee trainee = traineeService.findByUsername(traineeUsername.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

            Trainer trainer = trainerService.findByUsername(trainerUsername.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

            TrainingType trainingType = trainingTypeService.findById(trainingTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Training type not found"));

            if (!trainee.getUser().isActive()) throw new IllegalArgumentException("Trainee is not active");
            if (!trainer.getUser().isActive()) throw new IllegalArgumentException("Trainer is not active");

            Training training = Training.builder()
                    .name(trainingName.trim())
                    .type(trainingType)
                    .date(trainingDate)
                    .duration(duration)
                    .trainee(trainee)
                    .trainer(trainer)
                    .build();

            trainingRepository.save(training);
            log.info("Training added successfully (ID: {})", training.getId());
            return training;

        } catch (Exception e) {
            log.error("Failed to add training");
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Training> findById(Long id) {
        if (id == null) return Optional.empty();
        return trainingRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                               String trainerName, String trainingTypeName) {
        if (isBlank(traineeUsername)) return List.of();

        try {
            return trainingRepository.findTraineeTrainings(
                    traineeUsername.trim(), fromDate, toDate, trainerName, trainingTypeName);
        } catch (Exception e) {
            log.warn("Error finding trainee trainings", e);
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        if (isBlank(trainerUsername)) return List.of();

        try {
            return trainingRepository.findTrainerTrainings(
                    trainerUsername.trim(), fromDate, toDate, traineeName);
        } catch (Exception e) {
            log.warn("Error finding trainer trainings", e);
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Training updateTraining(Training training, String authenticatedUsername, String password) {
        authenticateOrThrow(authenticatedUsername, password);

        if (training == null || training.getId() == null) {
            throw new IllegalArgumentException("Training or training ID cannot be null");
        }

        validateTrainingFields(
                training.getTrainee().getUser().getUsername(),
                training.getTrainer().getUser().getUsername(),
                training.getName(),
                training.getType() != null ? training.getType().getId() : null,
                training.getDate(),
                training.getDuration()
        );

        try {
            trainingRepository.findById(training.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Training not found"));

            training.setName(training.getName().trim());
            trainingRepository.save(training);
            log.info("Training updated successfully (ID: {})", training.getId());
            return training;

        } catch (Exception e) {
            log.error("Failed to update training");
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public boolean deleteTraining(Training training, String authenticatedUsername, String password) {
        if (training == null) return false;

        authenticateOrThrow(authenticatedUsername, password);

        try {
            trainingRepository.delete(training);
            log.info("Training deleted successfully (ID: {})", training.getId());
            return true;
        } catch (Exception e) {
            log.error("Failed to delete training", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public boolean deleteTrainingById(Long trainingId, String authenticatedUsername, String password) {
        if (trainingId == null) return false;

        Optional<Training> trainingOptional = findById(trainingId);
        return trainingOptional.map(t -> deleteTraining(t, authenticatedUsername, password)).orElse(false);
    }

    private User authenticateOrThrow(String username, String password) {
        return authenticationService.authenticate(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Authentication failed"));
    }

    private void validateTrainingFields(String traineeUsername, String trainerUsername, String trainingName,
                                        Long trainingTypeId, LocalDate trainingDate, Integer duration) {
        if (isBlank(traineeUsername)) throw new IllegalArgumentException("Trainee username cannot be null or empty");
        if (isBlank(trainerUsername)) throw new IllegalArgumentException("Trainer username cannot be null or empty");
        if (isBlank(trainingName)) throw new IllegalArgumentException("Training name cannot be null or empty");
        if (trainingTypeId == null) throw new IllegalArgumentException("Training type ID cannot be null");
        if (trainingDate == null) throw new IllegalArgumentException("Training date cannot be null");
        if (duration == null || duration <= 0) throw new IllegalArgumentException("Training duration must be positive");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}