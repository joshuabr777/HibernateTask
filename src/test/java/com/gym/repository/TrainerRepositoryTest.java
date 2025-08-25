package com.gym.repository;

import com.gym.entity.Trainer;
import com.gym.repository.impl.TrainerRepositoryImpl;
import com.gym.util.TestUtils;

import org.junit.jupiter.api.*;
import jakarta.persistence.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainerRepositoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private TrainerRepository trainerRepository;
    
    @BeforeAll
    void init() {
        emf = Persistence.createEntityManagerFactory("test-gym-app");
    }
    
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        trainerRepository = new TrainerRepositoryImpl();
        TestUtils.injectEntityManager(trainerRepository, em);
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
    void testFindById_existingTrainer() {
        Optional<Trainer> trainerOpt = trainerRepository.findById(1L);
        assertTrue(trainerOpt.isPresent(), "Trainer should be found");
        assertEquals("trainer_user", trainerOpt.get().getUser().getUsername());
        assertEquals("Jane", trainerOpt.get().getUser().getFirstName());
    }
    
    @Test
    void testFindById_nonExistingTrainer() {
        Optional<Trainer> trainerOpt = trainerRepository.findById(999L);
        assertTrue(trainerOpt.isEmpty(), "Trainer should not be found with non-existing ID");
    }
    
    @Test
    void testFindByUsername_existingTrainer() {
        Optional<Trainer> trainerOpt = trainerRepository.findByUsername("trainer_user");
        assertTrue(trainerOpt.isPresent(), "Trainer should be found by username");
        assertEquals("Jane", trainerOpt.get().getUser().getFirstName());
        assertEquals("Smith", trainerOpt.get().getUser().getLastName());
    }
    
    @Test
    void testFindByUsername_nonExistingTrainer() {
        Optional<Trainer> trainerOpt = trainerRepository.findByUsername("non_existing_trainer");
        assertTrue(trainerOpt.isEmpty(), "Trainer should not be found with non-existing username");
    }
    
    @Test
    void testSave_newTrainer() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        // First create a user for the trainer
        var user = em.find(com.gym.entity.User.class, 4L); // Sarah Johnson from import.sql
        var specialization = em.find(com.gym.entity.TrainingType.class, 2L); // Cardio
        assertNotNull(user, "User should exist for this test");
        assertNotNull(specialization, "Specialization should exist for this test");
        
        Trainer newTrainer = Trainer.builder()
            .user(user)
            .specialization(specialization)
            .build();
            
        trainerRepository.save(newTrainer);
        tx.commit();
        
        Optional<Trainer> savedTrainer = trainerRepository.findByUsername("trainee_sarah");
        assertTrue(savedTrainer.isPresent(), "New trainer should be persisted");
        assertEquals("Cardio", savedTrainer.get().getSpecialization().getName());
    }
    
    @Test
    @Order(1)
    void testSave_updateExistingTrainer() {
        Optional<Trainer> existingTrainer = trainerRepository.findById(1L);
        assertTrue(existingTrainer.isPresent(), "Trainer should exist from import.sql");
        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        Trainer trainer = existingTrainer.get();
        var newSpecialization = em.find(com.gym.entity.TrainingType.class, 2L); // Cardio
        var originalSpecialization = trainer.getSpecialization();
        
        trainer.setSpecialization(newSpecialization);
        trainerRepository.save(trainer);
        tx.commit();
        
        Optional<Trainer> updatedTrainer = trainerRepository.findById(1L);
        assertTrue(updatedTrainer.isPresent(), "Updated trainer should exist");
        assertEquals("Cardio", updatedTrainer.get().getSpecialization().getName());
        assertNotEquals(originalSpecialization.getName(), updatedTrainer.get().getSpecialization().getName());
    }
    
    @Test
    void testDelete_existingTrainer() {
        // Create a trainer without dependencies for safe deletion
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        var user = em.find(com.gym.entity.User.class, 4L); // Sarah Johnson from import.sql
        var specialization = em.find(com.gym.entity.TrainingType.class, 3L); // Yoga
        assertNotNull(user, "User should exist for this test");
        assertNotNull(specialization, "Specialization should exist for this test");
        
        Trainer trainerToDelete = Trainer.builder()
            .user(user)
            .specialization(specialization)
            .build();
            
        trainerRepository.save(trainerToDelete);
        tx.commit();
        
        // Verify it exists
        Optional<Trainer> savedTrainer = trainerRepository.findByUsername("trainee_sarah");
        assertTrue(savedTrainer.isPresent(), "Trainer should exist before deletion");
        
        // Delete it
        tx = em.getTransaction();
        tx.begin();
        trainerRepository.delete(savedTrainer.get());
        tx.commit();
        
        // Verify deletion
        Optional<Trainer> deletedTrainer = trainerRepository.findByUsername("trainee_sarah");
        assertTrue(deletedTrainer.isEmpty(), "Trainer should be deleted");
    }
}