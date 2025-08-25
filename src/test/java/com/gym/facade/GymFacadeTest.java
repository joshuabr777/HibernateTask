package com.gym.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;
import com.gym.entity.User;
import com.gym.facade.impl.GymFacadeImpl;
import com.gym.service.TraineeService;
import com.gym.service.TrainerService;
import com.gym.service.TrainingService;
import com.gym.service.TrainingTypeService;

class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainingTypeService trainingTypeService;

    private GymFacade gymFacade;

    private Trainee mockTrainee;
    private Trainer mockTrainer;
    private Training mockTraining;
    private TrainingType mockTrainingType;
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gymFacade = new GymFacadeImpl(traineeService, trainerService, trainingService, trainingTypeService);

        // Initialize mock entities
        mockUser = User.builder()
            .id(1L)
            .username("john.doe")
            .password("password123")
            .firstName("John")
            .lastName("Doe")
            .isActive(true)
            .build();

        mockTrainingType = TrainingType.builder()
            .id(1L)
            .name("Strength")
            .build();

        mockTrainee = Trainee.builder()
            .id(1L)
            .user(mockUser)
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .address("123 Main St")
            .build();

        mockTrainer = Trainer.builder()
            .id(1L)
            .user(mockUser)
            .specialization(mockTrainingType)
            .build();

        mockTraining = Training.builder()
            .id(1L)
            .name("Morning Workout")
            .type(mockTrainingType)
            .date(LocalDate.now())
            .duration(60)
            .trainee(mockTrainee)
            .trainer(mockTrainer)
            .build();
    }

    // ---------------- Trainee Tests ----------------

    @Test
    void createTrainee_ValidData_ReturnsTrainee() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";

        when(traineeService.createTrainee(firstName, lastName, dateOfBirth, address))
            .thenReturn(mockTrainee);

        // When
        Trainee result = gymFacade.createTrainee(firstName, lastName, dateOfBirth, address);

        // Then
        assertNotNull(result);
        assertEquals(mockTrainee, result);
        verify(traineeService).createTrainee(firstName, lastName, dateOfBirth, address);
    }

    @Test
    void authenticateTrainee_ValidCredentials_ReturnsTrainee() {
        // Given
        String username = "john.doe";
        String password = "password123";

        when(traineeService.authenticateTrainee(username, password))
            .thenReturn(Optional.of(mockTrainee));

        // When
        Optional<Trainee> result = gymFacade.authenticateTrainee(username, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockTrainee, result.get());
        verify(traineeService).authenticateTrainee(username, password);
    }

    @Test
    void authenticateTrainee_InvalidCredentials_ReturnsEmpty() {
        // Given
        String username = "john.doe";
        String password = "wrongpassword";

        when(traineeService.authenticateTrainee(username, password))
            .thenReturn(Optional.empty());

        // When
        Optional<Trainee> result = gymFacade.authenticateTrainee(username, password);

        // Then
        assertTrue(result.isEmpty());
        verify(traineeService).authenticateTrainee(username, password);
    }

    @Test
    void updateTrainee_ValidData_ReturnsUpdatedTrainee() {
        // Given
        String username = "john.doe";
        String password = "password123";

        when(traineeService.updateTrainee(username, password, mockTrainee))
            .thenReturn(mockTrainee);

        // When
        Trainee result = gymFacade.updateTrainee(username, password, mockTrainee);

        // Then
        assertNotNull(result);
        assertEquals(mockTrainee, result);
        verify(traineeService).updateTrainee(username, password, mockTrainee);
    }

    @Test
    void changeTraineePassword_ValidCredentials_ReturnsTrue() {
        // Given
        String username = "john.doe";
        String oldPassword = "password123";
        String newPassword = "newpassword";

        when(traineeService.changePassword(username, oldPassword, newPassword))
            .thenReturn(true);

        // When
        boolean result = gymFacade.changeTraineePassword(username, oldPassword, newPassword);

        // Then
        assertTrue(result);
        verify(traineeService).changePassword(username, oldPassword, newPassword);
    }

    @Test
    void activateTrainee_ValidCredentials_ReturnsTrue() {
        // Given
        String username = "john.doe";
        String password = "password123";

        when(traineeService.activateTrainee(username, password))
            .thenReturn(true);

        // When
        boolean result = gymFacade.activateTrainee(username, password);

        // Then
        assertTrue(result);
        verify(traineeService).activateTrainee(username, password);
    }

    @Test
    void deactivateTrainee_ValidCredentials_ReturnsTrue() {
        // Given
        String username = "john.doe";
        String password = "password123";

        when(traineeService.deactivateTrainee(username, password))
            .thenReturn(true);

        // When
        boolean result = gymFacade.deactivateTrainee(username, password);

        // Then
        assertTrue(result);
        verify(traineeService).deactivateTrainee(username, password);
    }

    @Test
    void deleteTrainee_ValidCredentials_ReturnsTrue() {
        // Given
        String username = "john.doe";
        String password = "password123";

        when(traineeService.deleteTrainee(username, password))
            .thenReturn(true);

        // When
        boolean result = gymFacade.deleteTrainee(username, password);

        // Then
        assertTrue(result);
        verify(traineeService).deleteTrainee(username, password);
    }

    @Test
    void getTraineeTrainings_ValidCredentials_ReturnsTrainingList() {
        // Given
        String username = "john.doe";
        String password = "password123";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        String trainerName = "Jane";
        String trainingTypeName = "Strength";

        List<Training> trainings = List.of(mockTraining);
        when(traineeService.getTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName))
            .thenReturn(trainings);

        // When
        List<Training> result = gymFacade.getTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName);

        // Then
        assertEquals(1, result.size());
        assertEquals(mockTraining, result.get(0));
        verify(traineeService).getTraineeTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Test
    void getUnassignedTrainers_ValidCredentials_ReturnsTrainerList() {
        // Given
        String username = "john.doe";
        String password = "password123";

        List<Trainer> trainers = List.of(mockTrainer);
        when(traineeService.getUnassignedTrainers(username, password))
            .thenReturn(trainers);

        // When
        List<Trainer> result = gymFacade.getUnassignedTrainers(username, password);

        // Then
        assertEquals(1, result.size());
        assertEquals(mockTrainer, result.get(0));
        verify(traineeService).getUnassignedTrainers(username, password);
    }

    @Test
    void updateTraineeTrainers_ValidData_ReturnsTrue() {
        // Given
        String username = "john.doe";
        String password = "password123";
        Set<Trainer> trainers = new HashSet<>();
        trainers.add(mockTrainer);

        when(traineeService.updateTraineeTrainers(username, password, trainers))
            .thenReturn(true);

        // When
        boolean result = gymFacade.updateTraineeTrainers(username, password, trainers);

        // Then
        assertTrue(result);
        verify(traineeService).updateTraineeTrainers(username, password, trainers);
    }

    // ---------------- Trainer Tests ----------------

    @Test
    void createTrainer_ValidData_ReturnsTrainer() {
        // Given
        String firstName = "Jane";
        String lastName = "Smith";
        String specializationName = "Strength";

        when(trainerService.createTrainer(firstName, lastName, specializationName))
            .thenReturn(mockTrainer);

        // When
        Trainer result = gymFacade.createTrainer(firstName, lastName, specializationName);

        // Then
        assertNotNull(result);
        assertEquals(mockTrainer, result);
        verify(trainerService).createTrainer(firstName, lastName, specializationName);
    }

    @Test
    void authenticateTrainer_ValidCredentials_ReturnsTrainer() {
        // Given
        String username = "jane.smith";
        String password = "password123";

        when(trainerService.authenticateTrainer(username, password))
            .thenReturn(Optional.of(mockTrainer));

        // When
        Optional<Trainer> result = gymFacade.authenticateTrainer(username, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockTrainer, result.get());
        verify(trainerService).authenticateTrainer(username, password);
    }

    @Test
    void updateTrainer_ValidData_ReturnsUpdatedTrainer() {
        // Given
        String username = "jane.smith";
        String password = "password123";

        when(trainerService.updateTrainer(username, password, mockTrainer))
            .thenReturn(mockTrainer);

        // When
        Trainer result = gymFacade.updateTrainer(username, password, mockTrainer);

        // Then
        assertNotNull(result);
        assertEquals(mockTrainer, result);
        verify(trainerService).updateTrainer(username, password, mockTrainer);
    }

    @Test
    void changeTrainerPassword_ValidCredentials_ReturnsTrue() {
        // Given
        String username = "jane.smith";
        String oldPassword = "password123";
        String newPassword = "newpassword";

        when(trainerService.changePassword(username, oldPassword, newPassword))
            .thenReturn(true);

        // When
        boolean result = gymFacade.changeTrainerPassword(username, oldPassword, newPassword);

        // Then
        assertTrue(result);
        verify(trainerService).changePassword(username, oldPassword, newPassword);
    }

    @Test
    void getTrainerTrainings_ValidCredentials_ReturnsTrainingList() {
        // Given
        String username = "jane.smith";
        String password = "password123";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        String traineeName = "John";

        List<Training> trainings = List.of(mockTraining);
        when(trainerService.getTrainerTrainings(username, password, fromDate, toDate, traineeName))
            .thenReturn(trainings);

        // When
        List<Training> result = gymFacade.getTrainerTrainings(username, password, fromDate, toDate, traineeName);

        // Then
        assertEquals(1, result.size());
        assertEquals(mockTraining, result.get(0));
        verify(trainerService).getTrainerTrainings(username, password, fromDate, toDate, traineeName);
    }

    // ---------------- Training Tests ----------------

    @Test
    void addTraining_ValidData_ReturnsTraining() {
        // Given
        String traineeUsername = "john.doe";
        String trainerUsername = "jane.smith";
        String trainingName = "Morning Workout";
        Long trainingTypeId = 1L;
        LocalDate trainingDate = LocalDate.now();
        Integer duration = 60;
        String authenticatedUsername = "admin";
        String password = "adminpass";

        when(trainingService.addTraining(traineeUsername, trainerUsername, trainingName, 
                                       trainingTypeId, trainingDate, duration, 
                                       authenticatedUsername, password))
            .thenReturn(mockTraining);

        // When
        Training result = gymFacade.addTraining(traineeUsername, trainerUsername, trainingName, 
                                              trainingTypeId, trainingDate, duration, 
                                              authenticatedUsername, password);

        // Then
        assertNotNull(result);
        assertEquals(mockTraining, result);
        verify(trainingService).addTraining(traineeUsername, trainerUsername, trainingName, 
                                           trainingTypeId, trainingDate, duration, 
                                           authenticatedUsername, password);
    }

    @Test
    void updateTraining_ValidData_ReturnsUpdatedTraining() {
        // Given
        String authenticatedUsername = "admin";
        String password = "adminpass";

        when(trainingService.updateTraining(mockTraining, authenticatedUsername, password))
            .thenReturn(mockTraining);

        // When
        Training result = gymFacade.updateTraining(mockTraining, authenticatedUsername, password);

        // Then
        assertNotNull(result);
        assertEquals(mockTraining, result);
        verify(trainingService).updateTraining(mockTraining, authenticatedUsername, password);
    }

    @Test
    void deleteTraining_ValidData_ReturnsTrue() {
        // Given
        String authenticatedUsername = "admin";
        String password = "adminpass";

        when(trainingService.deleteTraining(mockTraining, authenticatedUsername, password))
            .thenReturn(true);

        // When
        boolean result = gymFacade.deleteTraining(mockTraining, authenticatedUsername, password);

        // Then
        assertTrue(result);
        verify(trainingService).deleteTraining(mockTraining, authenticatedUsername, password);
    }

    @Test
    void deleteTrainingById_ValidData_ReturnsTrue() {
        // Given
        Long trainingId = 1L;
        String authenticatedUsername = "admin";
        String password = "adminpass";

        when(trainingService.deleteTrainingById(trainingId, authenticatedUsername, password))
            .thenReturn(true);

        // When
        boolean result = gymFacade.deleteTrainingById(trainingId, authenticatedUsername, password);

        // Then
        assertTrue(result);
        verify(trainingService).deleteTrainingById(trainingId, authenticatedUsername, password);
    }

    // ---------------- TrainingType Tests ----------------

    @Test
    void findTrainingTypeById_ValidId_ReturnsTrainingType() {
        // Given
        Long id = 1L;

        when(trainingTypeService.findById(id))
            .thenReturn(Optional.of(mockTrainingType));

        // When
        Optional<TrainingType> result = gymFacade.findTrainingTypeById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockTrainingType, result.get());
        verify(trainingTypeService).findById(id);
    }

    @Test
    void findTrainingTypeByName_ValidName_ReturnsTrainingType() {
        // Given
        String name = "Strength";

        when(trainingTypeService.findByName(name))
            .thenReturn(Optional.of(mockTrainingType));

        // When
        Optional<TrainingType> result = gymFacade.findTrainingTypeByName(name);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockTrainingType, result.get());
        verify(trainingTypeService).findByName(name);
    }

    @Test
    void getAllTrainingTypes_ReturnsTrainingTypeList() {
        // Given
        List<TrainingType> trainingTypes = List.of(mockTrainingType);

        when(trainingTypeService.findAll())
            .thenReturn(trainingTypes);

        // When
        List<TrainingType> result = gymFacade.getAllTrainingTypes();

        // Then
        assertEquals(1, result.size());
        assertEquals(mockTrainingType, result.get(0));
        verify(trainingTypeService).findAll();
    }

    @Test
    void findTrainingTypeById_InvalidId_ReturnsEmpty() {
        // Given
        Long id = 999L;

        when(trainingTypeService.findById(id))
            .thenReturn(Optional.empty());

        // When
        Optional<TrainingType> result = gymFacade.findTrainingTypeById(id);

        // Then
        assertTrue(result.isEmpty());
        verify(trainingTypeService).findById(id);
    }

    @Test
    void findTrainingTypeByName_InvalidName_ReturnsEmpty() {
        // Given
        String name = "NonExistent";

        when(trainingTypeService.findByName(name))
            .thenReturn(Optional.empty());

        // When
        Optional<TrainingType> result = gymFacade.findTrainingTypeByName(name);

        // Then
        assertTrue(result.isEmpty());
        verify(trainingTypeService).findByName(name);
    }
}