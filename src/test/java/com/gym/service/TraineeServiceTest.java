package com.gym.service;

import com.gym.entity.Trainee;
import com.gym.entity.User;
import com.gym.repository.TraineeRepository;
import com.gym.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private UserService userService;
    @Mock private AuthenticationService authenticationService;
    @Mock private TrainingService trainingService;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeService = new TraineeServiceImpl(
                traineeRepository, userService, authenticationService, trainingService
        );
    }

    @Test
    void createTrainee_success() {
        User user = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .build();

        when(userService.createUser("Jane", "Smith")).thenReturn(user);
        doNothing().when(traineeRepository).save(any(Trainee.class));

        Trainee result = traineeService.createTrainee("Jane", "Smith",
                LocalDate.of(2000, 1, 1), "Address");

        assertNotNull(result);
        assertEquals("Jane", result.getUser().getFirstName());
        assertEquals(LocalDate.of(2000, 1, 1), result.getDateOfBirth());

        verify(userService).createUser("Jane", "Smith");
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void authenticateTrainee_success() {
        User user = User.builder().id(1L).username("trainee1").password("123").build();
        Trainee trainee = Trainee.builder().id(10L).user(user).build();

        when(authenticationService.authenticate("trainee1", "123")).thenReturn(Optional.of(user));
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.authenticateTrainee("trainee1", "123");

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void authenticateTrainee_wrongPassword() {
        when(authenticationService.authenticate("trainee1", "wrong"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.authenticateTrainee("trainee1", "wrong")
        );

        assertEquals("Authentication failed for username: trainee1", ex.getMessage());
        verify(traineeRepository, never()).findByUsername(anyString());
    }
}