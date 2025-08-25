package com.gym.facade.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;
import com.gym.facade.GymFacade;
import com.gym.service.TraineeService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import com.gym.service.TrainingTypeService;

@Component
public class GymFacadeImpl implements GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public GymFacadeImpl(TraineeService traineeService,
                         TrainerService trainerService,
                         TrainingService trainingService,
                         TrainingTypeService trainingTypeService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }

    // ---------------- Trainee ----------------
    @Override
    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    @Override
    public Optional<Trainee> authenticateTrainee(String username, String password) {
        return traineeService.authenticateTrainee(username, password);
    }

    @Override
    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        return traineeService.updateTrainee(username, password, trainee);
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
    public List<Training> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        return traineeService.getTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Override
    public List<Trainer> getUnassignedTrainers(String username, String password) {
        return traineeService.getUnassignedTrainers(username, password);
    }

    @Override
    public boolean updateTraineeTrainers(String username, String password, Set<Trainer> trainers) {
        return traineeService.updateTraineeTrainers(username, password, trainers);
    }

    // ---------------- Trainer ----------------
    @Override
    public Trainer createTrainer(String firstName, String lastName, String specializationName) {
        return trainerService.createTrainer(firstName, lastName, specializationName);
    }

    @Override
    public Optional<Trainer> authenticateTrainer(String username, String password) {
        return trainerService.authenticateTrainer(username, password);
    }

    @Override
    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        return trainerService.updateTrainer(username, password, trainer);
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
    public List<Training> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainerService.getTrainerTrainings(username, password, fromDate, toDate, traineeName);
    }

    // ---------------- Training ----------------
    @Override
    public Training addTraining(String traineeUsername, String trainerUsername, String trainingName, Long trainingTypeId, LocalDate trainingDate, Integer duration, String authenticatedUsername, String password) {
        return trainingService.addTraining(traineeUsername, trainerUsername, trainingName, trainingTypeId, trainingDate, duration, authenticatedUsername, password);
    }

    @Override
    public Training updateTraining(Training training, String authenticatedUsername, String password) {
        return trainingService.updateTraining(training, authenticatedUsername, password);
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
    public Optional<TrainingType> findTrainingTypeById(Long id) {
        return trainingTypeService.findById(id);
    }

    @Override
    public Optional<TrainingType> findTrainingTypeByName(String name) {
        return trainingTypeService.findByName(name);
    }

    @Override
    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeService.findAll();
    }
}