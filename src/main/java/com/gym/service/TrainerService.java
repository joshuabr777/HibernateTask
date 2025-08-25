package com.gym.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;

public interface TrainerService {

    boolean changePassword(String username, String oldPassword, String newPassword);

    Trainer updateTrainer(String username, String password, Trainer trainer);

    boolean activateTrainer(String username, String password);

    boolean deactivateTrainer(String username, String password);

    List<Training> getTrainerTrainings(String username, String password,
                                       LocalDate fromDate, LocalDate toDate,
                                       String traineeName);

    Trainer createTrainer(String firstName, String lastName, TrainingType specialization);

    Trainer createTrainer(String firstName, String lastName, String specializationName);

    Optional<Trainer> authenticateTrainer(String username, String password);

    Optional<Trainer> findByUsername(String username);

    Optional<Trainer> findById(Long id);
}