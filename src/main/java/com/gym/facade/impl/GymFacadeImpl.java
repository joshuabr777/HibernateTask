package com.gym.facade.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;
import com.gym.facade.GymFacade;
import com.gym.service.TraineeService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import com.gym.service.TrainingTypeService;
import com.gym.util.mapper.TraineeMapper;
import com.gym.util.mapper.TrainerMapper;
import com.gym.util.mapper.TrainingMapper;
import com.gym.util.mapper.TrainingTypeMapper;

@Component
public class GymFacadeImpl implements GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;

    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;

    @Autowired
    public GymFacadeImpl(TraineeService traineeService,
                         TrainerService trainerService,
                         TrainingService trainingService,
                         TrainingTypeService trainingTypeService,
                         TraineeMapper traineeMapper,
                         TrainerMapper trainerMapper,
                         TrainingMapper trainingMapper,
                         TrainingTypeMapper trainingTypeMapper) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
        this.trainingTypeMapper = trainingTypeMapper;
    }

    // ---------------- Trainee ----------------
    @Override
    public TraineeRegistrationResponse createTrainee(TraineeRegistrationRequest request) {
        Trainee trainee = traineeMapper.toEntity(request);
        Trainee created = traineeService.createTrainee(
                trainee,
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName()
        );
        return traineeMapper.toRegistrationResponse(created);
    }

    @Override
    public TraineeProfileResponse updateTrainee(String username, String password, UpdateTraineeProfileRequest request) {
        Trainee trainee = traineeMapper.toEntity(request);
        Trainee updated = traineeService.updateTrainee(username, password, trainee);
        return traineeMapper.toProfileResponse(updated);
    }

    @Override
    public boolean changeTraineePassword(String username, String oldPassword, String newPassword) {
        return traineeService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public boolean activateTrainee(String username, String password) {
        return traineeService.activateTrainee(username, password);
    }

    @Override
    public boolean deactivateTrainee(String username, String password) {
        return traineeService.deactivateTrainee(username, password);
    }

    @Override
    public boolean deleteTrainee(String username, String password) {
        return traineeService.deleteTrainee(username, password);
    }

    @Override
    public List<TrainingSummary> getTraineeTrainings(String username, String password,
                                                     LocalDate fromDate, LocalDate toDate,
                                                     String trainerName, String trainingTypeName) {
        return trainingMapper.toSummaryList(
                traineeService.getTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName)
        );
    }

    @Override
    public List<TrainerSummary> getUnassignedTrainers(String username, String password) {
        return trainerMapper.toSummaryList(traineeService.getUnassignedTrainers(username, password));
    }

    @Override
    public boolean updateTraineeTrainers(String username, String password, UpdateTraineeTrainersRequest request) {
        Set<Trainer> trainers = request.getTrainerUsernames().stream()
                .map(trainerService::findByUsername)
                .collect(Collectors.toSet());
        return traineeService.updateTraineeTrainers(username, password, trainers);
    }

    // ---------------- Trainer ----------------
    @Override
    public TrainerRegistrationResponse createTrainer(TrainerRegistrationRequest request) {
        Trainer trainer = trainerMapper.toEntity(request);
        Trainer created = trainerService.createTrainer(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername()
        );
        return trainerMapper.toRegistrationResponse(created);
    }

    @Override
    public TrainerProfileResponse updateTrainer(String username, String password, UpdateTrainerProfileRequest request) {
        Trainer trainer = trainerMapper.toEntity(request);
        Trainer updated = trainerService.updateTrainer(username, password, trainer);
        return trainerMapper.toProfileResponse(updated);
    }

    @Override
    public boolean changeTrainerPassword(String username, String oldPassword, String newPassword) {
        return trainerService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public boolean activateTrainer(String username, String password) {
        return trainerService.activateTrainer(username, password);
    }

    @Override
    public boolean deactivateTrainer(String username, String password) {
        return trainerService.deactivateTrainer(username, password);
    }

    @Override
    public List<TrainingSummary> getTrainerTrainings(String username, String password,
                                                     LocalDate fromDate, LocalDate toDate,
                                                     String traineeName) {
        return trainingMapper.toSummaryList(
                trainerService.getTrainerTrainings(username, password, fromDate, toDate, traineeName)
        );
    }

    // ---------------- Training ----------------
    @Override
    public TrainingSummary addTraining(AddTrainingRequest request, String authenticatedUsername, String password) {
        Training training = trainingMapper.toEntity(request);
        Training created = trainingService.addTraining(
                training,
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingType(),
                request.getTrainingDate(),
                authenticatedUsername,
                password
        );
        return trainingMapper.toSummary(created);
    }

    @Override
    public TrainingSummary updateTraining(Training training, String authenticatedUsername, String password) {
        Training updated = trainingService.updateTraining(training, authenticatedUsername, password);
        return trainingMapper.toSummary(updated);
    }

    @Override
    public boolean deleteTraining(Training training, String authenticatedUsername, String password) {
        return trainingService.deleteTraining(training, authenticatedUsername, password);
    }

    @Override
    public boolean deleteTrainingById(Long trainingId, String authenticatedUsername, String password) {
        return trainingService.deleteTrainingById(trainingId, authenticatedUsername, password);
    }

    // ---------------- TrainingType ----------------
    @Override
    public TrainingTypeResponse findTrainingTypeById(Long id) {
        return trainingTypeService.findById(id)
                .map(trainingTypeMapper::toResponse)
                .orElse(null);
    }

    @Override
    public TrainingTypeResponse findTrainingTypeByName(String name) {
        return trainingTypeService.findByName(name)
                .map(trainingTypeMapper::toResponse)
                .orElse(null);
    }

    @Override
    public List<TrainingTypeResponse> getAllTrainingTypes() {
        return trainingTypeMapper.toResponseList(trainingTypeService.findAll());
    }
}