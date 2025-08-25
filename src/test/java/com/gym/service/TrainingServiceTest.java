package com.gym.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.entity.Training;
import com.gym.entity.TrainingType;
import com.gym.entity.User;
import com.gym.repository.TrainingRepository;
import com.gym.service.impl.TrainingServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainingServiceTest {

    private TrainingRepository trainingRepository;
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingTypeService trainingTypeService;
    private AuthenticationService authenticationService;
    private TrainingService trainingService;

    @BeforeAll
    void init() {
        trainingRepository = Mockito.mock(TrainingRepository.class);
        traineeService = Mockito.mock(TraineeService.class);
        trainerService = Mockito.mock(TrainerService.class);
        trainingTypeService = Mockito.mock(TrainingTypeService.class);
        authenticationService = Mockito.mock(AuthenticationService.class);

        trainingService = new TrainingServiceImpl(
                trainingRepository,
                authenticationService,
                traineeService,
                trainerService,
                trainingTypeService
        );
    }

    @Test
    void testAddTraining_success() {
        User authUser = User.builder().username("admin").password("pass").isActive(true).build();
        Mockito.when(authenticationService.authenticate("admin", "pass")).thenReturn(Optional.of(authUser));

        Trainee trainee = Trainee.builder().user(User.builder().username("trainee1").isActive(true).build()).build();
        Mockito.when(traineeService.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        Trainer trainer = Trainer.builder().user(User.builder().username("trainer1").isActive(true).build()).build();
        Mockito.when(trainerService.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        TrainingType type = TrainingType.builder().id(1L).name("Yoga").build();
        Mockito.when(trainingTypeService.findById(1L)).thenReturn(Optional.of(type));

        doNothing().when(trainingRepository).save(Mockito.any(Training.class));

        Training result = trainingService.addTraining(
                "trainee1", "trainer1", "Morning Yoga", 1L, LocalDate.now(), 60, "admin", "pass"
        );

        assertNotNull(result);
        assertEquals("Morning Yoga", result.getName());
    }

    @Test
    void testAddTraining_authFail() {
        Mockito.when(authenticationService.authenticate("admin", "wrong")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            trainingService.addTraining("trainee1", "trainer1", "Yoga", 1L, LocalDate.now(), 60, "admin", "wrong");
        });

        assertEquals("Authentication failed", ex.getMessage());
    }

    @Test
    void testFindTraineeTrainings_returnsList() {
        List<Training> mockList = List.of(Training.builder().name("Yoga").build());
        Mockito.when(trainingRepository.findTraineeTrainings("trainee1", null, null, null, null)).thenReturn(mockList);

        List<Training> result = trainingService.findTraineeTrainings("trainee1", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Yoga", result.get(0).getName());
    }
}

