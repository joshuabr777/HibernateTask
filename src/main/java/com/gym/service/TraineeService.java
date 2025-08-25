package com.gym.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;

public interface TraineeService {
    Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address);

    Optional<Trainee> authenticateTrainee(String username, String password);

    Optional<Trainee> findByUsername(String username);

    Optional<Trainee> findById(Long id);

    boolean changePassword(String username, String oldPassword, String newPassword);

    Trainee updateTrainee(String username, String password, Trainee trainee);

    boolean activateTrainee(String username, String password);

    boolean deactivateTrainee(String username, String password);

    boolean deleteTrainee(String username, String password);

    List<Training> getTraineeTrainings(String username, String password,
                                       LocalDate fromDate, LocalDate toDate,
                                       String trainerName, String trainingTypeName);

    List<Trainer> getUnassignedTrainers(String username, String password);

    boolean updateTraineeTrainers(String username, String password, Set<Trainer> trainers);
}
