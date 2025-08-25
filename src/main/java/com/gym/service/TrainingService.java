package com.gym.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gym.entity.Training;

public interface TrainingService {

    /**
     * Adds a new training.
     *
     * @param traineeUsername the trainee's username
     * @param trainerUsername the trainer's username
     * @param trainingName the training name
     * @param trainingTypeId the training type ID
     * @param trainingDate the training date
     * @param duration the training duration in minutes
     * @param authenticatedUsername the username for authentication
     * @param password the password for authentication
     * @return the created training
     * @throws IllegalArgumentException if authentication or validation fails
     */
    Training addTraining(String traineeUsername, String trainerUsername, String trainingName,
                         Long trainingTypeId, LocalDate trainingDate, Integer duration,
                         String authenticatedUsername, String password);

    /**
     * Finds a training by its ID.
     *
     * @param id the training ID
     * @return an Optional containing the training if found
     */
    Optional<Training> findById(Long id);

    /**
     * Finds trainings for a specific trainee by filter criteria.
     *
     * @param traineeUsername the trainee's username
     * @param fromDate optional start date filter
     * @param toDate optional end date filter
     * @param trainerName optional trainer name filter
     * @param trainingTypeName optional training type name filter
     * @return list of matching trainings
     */
    List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                        String trainerName, String trainingTypeName);

    /**
     * Finds trainings for a specific trainer by filter criteria.
     *
     * @param trainerUsername the trainer's username
     * @param fromDate optional start date filter
     * @param toDate optional end date filter
     * @param traineeName optional trainee name filter
     * @return list of matching trainings
     */
    List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                        String traineeName);

    /**
     * Updates an existing training.
     *
     * @param training the training to update
     * @param authenticatedUsername the username for authentication
     * @param password the password for authentication
     * @return the updated training
     * @throws IllegalArgumentException if authentication or validation fails
     */
    Training updateTraining(Training training, String authenticatedUsername, String password);

    /**
     * Deletes a training.
     *
     * @param training the training to delete
     * @param authenticatedUsername the username for authentication
     * @param password the password for authentication
     * @return true if successfully deleted, false otherwise
     */
    boolean deleteTraining(Training training, String authenticatedUsername, String password);

    /**
     * Deletes a training by ID.
     *
     * @param trainingId the training ID
     * @param authenticatedUsername the username for authentication
     * @param password the password for authentication
     * @return true if successfully deleted, false otherwise
     */
    boolean deleteTrainingById(Long trainingId, String authenticatedUsername, String password);
}