package com.gym.repository;

import com.gym.entity.TrainingType;
import com.gym.repository.impl.TrainingTypeRepositoryImpl;
import com.gym.util.TestUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainingTypeRepositoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private TrainingTypeRepository trainingTypeRepository;
    
    @BeforeAll
    void init() {
        emf = Persistence.createEntityManagerFactory("test-gym-app");
    }
    
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        trainingTypeRepository = new TrainingTypeRepositoryImpl();
        TestUtils.injectEntityManager(trainingTypeRepository, em);
    }
    
    @AfterEach
    void tearDown() {
        if (em != null) {
            em.close();
        }
    }
    
    @AfterAll
    void close() {
        if (emf != null) {
            emf.close();
        }
    }
    
    @Test
    void testFindById_existingTrainingType() {
        Optional<TrainingType> trainingTypeOpt = trainingTypeRepository.findById(1L);
        assertTrue(trainingTypeOpt.isPresent(), "TrainingType should be found");
        assertEquals("Strength", trainingTypeOpt.get().getName());
    }
    
    @Test
    void testFindById_nonExistingTrainingType() {
        Optional<TrainingType> trainingTypeOpt = trainingTypeRepository.findById(999L);
        assertTrue(trainingTypeOpt.isEmpty(), "TrainingType should not be found with non-existing ID");
    }
    
    @Test
    void testFindByName_existingTrainingType() {
        Optional<TrainingType> trainingTypeOpt = trainingTypeRepository.findByName("Strength");
        assertTrue(trainingTypeOpt.isPresent(), "TrainingType should be found by name");
        assertEquals("Strength", trainingTypeOpt.get().getName());
    }
    
    @Test
    void testFindByName_nonExistingTrainingType() {
        Optional<TrainingType> trainingTypeOpt = trainingTypeRepository.findByName("NonExistingType");
        assertTrue(trainingTypeOpt.isEmpty(), "TrainingType should not be found with non-existing name");
    }
    
    @Test
    void testFindByName_caseMatters() {
        Optional<TrainingType> trainingTypeOpt = trainingTypeRepository.findByName("strength");
        assertTrue(trainingTypeOpt.isEmpty(), "TrainingType search should be case sensitive");
    }
    
    @Test
    void testFindAll() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        
        assertNotNull(trainingTypes, "TrainingTypes list should not be null");
        assertFalse(trainingTypes.isEmpty(), "Should find training types from import.sql");
        assertTrue(trainingTypes.size() >= 3, "Should find at least 3 training types");
        
        // Collect names
        List<String> typeNames = trainingTypes.stream()
                .map(TrainingType::getName)
                .toList();
        
        // Expected training types (import.sql has these)
        assertTrue(typeNames.contains("Strength"), "Expected 'Strength' type to exist");
        assertTrue(typeNames.contains("Cardio"), "Expected 'Cardio' type to exist");
        assertTrue(typeNames.contains("Yoga"), "Expected 'Yoga' type to exist");
    }
}
