package com.gym.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gym.entity.Training;

public interface TrainingRepository {
    void save(Training training);
    Optional<Training> findById(Long id);
    List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName);
    List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);
    void delete(Training training);
}
