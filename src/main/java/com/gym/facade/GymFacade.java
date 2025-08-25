package com.gym.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;

public interface GymFacade {

    // Trainee operations
    Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address);
    Optional<Trainee> authenticateTrainee(String username, String password);
    Trainee updateTrainee(String username, String password, Trainee trainee);
    boolean changeTraineePassword(String username, String oldPassword, String newPassword);
    boolean activateTrainee(String username, String password);
    boolean deactivateTrainee(String username, String password);
    boolean deleteTrainee(String username, String password);
    List<Training> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName);
    List<Trainer> getUnassignedTrainers(String username, String password);
    boolean updateTraineeTrainers(String username, String password, Set<Trainer> trainers);

    // Trainer operations
    Trainer createTrainer(String firstName, String lastName, String specializationName);
    Optional<Trainer> authenticateTrainer(String username, String password);
    Trainer updateTrainer(String username, String password, Trainer trainer);
    boolean changeTrainerPassword(String username, String oldPassword, String newPassword);
    boolean activateTrainer(String username, String password);
    boolean deactivateTrainer(String username, String password);
    List<Training> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName);

    // Training operations
    Training addTraining(String traineeUsername, String trainerUsername, String trainingName, Long trainingTypeId, LocalDate trainingDate, Integer duration, String authenticatedUsername, String password);
    Training updateTraining(Training training, String authenticatedUsername, String password);
    boolean deleteTraining(Training training, String authenticatedUsername, String password);
    boolean deleteTrainingById(Long trainingId, String authenticatedUsername, String password);

    // TrainingType operations
    Optional<TrainingType> findTrainingTypeById(Long id);
    Optional<TrainingType> findTrainingTypeByName(String name);
    List<TrainingType> getAllTrainingTypes();
}

