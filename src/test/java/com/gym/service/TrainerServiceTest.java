package com.gym.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gym.entity.Trainer;
import com.gym.entity.TrainingType;
import com.gym.entity.User;
import com.gym.repository.TrainerRepository;
import com.gym.service.impl.TrainerServiceImpl;

class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private UserService userService;
    @Mock private AuthenticationService authenticationService;
    @Mock private TrainingService trainingService;
    @Mock private TrainingTypeService trainingTypeService;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainerService = new TrainerServiceImpl(
            trainerRepository,
            userService,
            authenticationService,
            trainingService,
            trainingTypeService
        );
    }

    @Test
    void createTrainer_shouldReturnTrainer_whenValidData() {
        TrainingType yoga = TrainingType.builder().id(1L).name("Yoga").build();
        User user = User.builder().id(10L).username("john.doe").firstName("John").lastName("Doe").build();

        when(userService.createUser("John", "Doe")).thenReturn(user);
        doNothing().when(trainerRepository).save(any(Trainer.class));

        Trainer result = trainerService.createTrainer("John", "Doe", yoga);

        assertNotNull(result);
        assertEquals("Yoga", result.getSpecialization().getName());
        assertEquals("john.doe", result.getUser().getUsername());

        verify(userService).createUser("John", "Doe");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void authenticateTrainer_shouldReturnTrainer_whenCredentialsValid() {
        User user = User.builder().id(10L).username("trainer1").password("pass").build();
        Trainer trainer = Trainer.builder().id(20L).user(user).build();

        when(authenticationService.authenticate("trainer1", "pass"))
            .thenReturn(Optional.of(user));
        when(trainerRepository.findByUsername("trainer1"))
            .thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.authenticateTrainer("trainer1", "pass");

        assertTrue(result.isPresent());
        assertEquals(20L, result.get().getId());
    }

    @Test
    void authenticateTrainer_shouldReturnEmpty_whenPasswordInvalid() {
        when(authenticationService.authenticate("trainer1", "wrong"))
            .thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.authenticateTrainer("trainer1", "wrong");

        assertTrue(result.isEmpty());
        verify(trainerRepository, never()).findByUsername(anyString());
    }
}
