package com.gym.facade;

import java.time.LocalDate;
import java.util.List;
import com.gym.dto.trainee.TraineeProfileResponse;
import com.gym.dto.trainee.TraineeRegistrationRequest;
import com.gym.dto.trainee.TraineeRegistrationResponse;
import com.gym.dto.trainee.UpdateTraineeProfileRequest;
import com.gym.dto.trainee.UpdateTraineeTrainersRequest;
import com.gym.dto.trainer.TrainerProfileResponse;
import com.gym.dto.trainer.TrainerRegistrationRequest;
import com.gym.dto.trainer.TrainerRegistrationResponse;
import com.gym.dto.trainer.TrainerSummary;
import com.gym.dto.trainer.UpdateTrainerProfileRequest;
import com.gym.dto.training.AddTrainingRequest;
import com.gym.dto.training.TrainingSummary;
import com.gym.dto.training_type.TrainingTypeResponse;
import com.gym.entity.Training;

public interface GymFacade {

    // ---------------- Trainee ----------------
    TraineeRegistrationResponse createTrainee(TraineeRegistrationRequest request);

    TraineeProfileResponse updateTrainee(String username, String password, UpdateTraineeProfileRequest request);

    boolean changeTraineePassword(String username, String oldPassword, String newPassword);

    boolean activateTrainee(String username, String password);

    boolean deactivateTrainee(String username, String password);

    boolean deleteTrainee(String username, String password);

    List<TrainingSummary> getTraineeTrainings(String username, String password,
            LocalDate fromDate, LocalDate toDate,
            String trainerName, String trainingTypeName);

    List<TrainerSummary> getUnassignedTrainers(String username, String password);

    boolean updateTraineeTrainers(String username, String password, UpdateTraineeTrainersRequest request);

    // ---------------- Trainer ----------------
    TrainerRegistrationResponse createTrainer(TrainerRegistrationRequest request);

    TrainerProfileResponse updateTrainer(String username, String password, UpdateTrainerProfileRequest request);

    boolean changeTrainerPassword(String username, String oldPassword, String newPassword);

    boolean activateTrainer(String username, String password);

    boolean deactivateTrainer(String username, String password);

    List<TrainingSummary> getTrainerTrainings(String username, String password,
            LocalDate fromDate, LocalDate toDate,
            String traineeName);

    // ---------------- Training ----------------
    TrainingSummary addTraining(AddTrainingRequest request, String authenticatedUsername, String password);

    TrainingSummary updateTraining(Training training, String authenticatedUsername, String password);

    boolean deleteTraining(Training training, String authenticatedUsername, String password);

    boolean deleteTrainingById(Long trainingId, String authenticatedUsername, String password);

    // ---------------- TrainingType ----------------
    TrainingTypeResponse findTrainingTypeById(Long id);

    TrainingTypeResponse findTrainingTypeByName(String name);

    List<TrainingTypeResponse> getAllTrainingTypes();
}
