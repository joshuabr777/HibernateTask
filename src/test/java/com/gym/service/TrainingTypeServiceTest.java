package com.gym.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import com.gym.entity.TrainingType;
import com.gym.repository.TrainingTypeRepository;
import com.gym.service.impl.TrainingTypeServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainingTypeServiceTest {

    private TrainingTypeRepository trainingTypeRepository;
    private TrainingTypeService trainingTypeService;

    @BeforeAll
    void init() {
        trainingTypeRepository = Mockito.mock(TrainingTypeRepository.class);
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeRepository);
    }

    @Test
    void testFindById_existing() {
        TrainingType yoga = TrainingType.builder().id(1L).name("Yoga").build();
        Mockito.when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(yoga));

        Optional<TrainingType> result = trainingTypeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getName());
    }

    @Test
    void testFindById_nonExisting() {
        Mockito.when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TrainingType> result = trainingTypeService.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll_returnsList() {
        List<TrainingType> mockList = List.of(
            TrainingType.builder().id(1L).name("Cardio").build(),
            TrainingType.builder().id(2L).name("Pilates").build()
        );
        Mockito.when(trainingTypeRepository.findAll()).thenReturn(mockList);

        List<TrainingType> result = trainingTypeService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testExistsByName_true() {
        Mockito.when(trainingTypeRepository.findByName("Strength"))
               .thenReturn(Optional.of(new TrainingType()));

        assertTrue(trainingTypeService.existsByName("Strength"));
    }

    @Test
    void testExistsByName_false() {
        Mockito.when(trainingTypeRepository.findByName("Unknown"))
               .thenReturn(Optional.empty());

        assertFalse(trainingTypeService.existsByName("Unknown"));
    }
}

