package com.gym.repository;

import com.gym.entity.Training;
import com.gym.repository.impl.TrainingRepositoryImpl;
import com.gym.util.TestUtils;

import org.junit.jupiter.api.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainingRepositoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private TrainingRepository trainingRepository;
    
    @BeforeAll
    void init() {
        emf = Persistence.createEntityManagerFactory("test-gym-app");
    }
    
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        trainingRepository = new TrainingRepositoryImpl();
        TestUtils.injectEntityManager(trainingRepository, em);
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
    void testFindById_existingTraining() {
        Optional<Training> trainingOpt = trainingRepository.findById(1L);
        assertTrue(trainingOpt.isPresent(), "Training should be found");
        assertEquals("Updated Morning Workout", trainingOpt.get().getName()); // assert the updated training name
        assertEquals(90, trainingOpt.get().getDuration()); // assert the updated duration
    }
    
    @Test
    void testFindById_nonExistingTraining() {
        Optional<Training> trainingOpt = trainingRepository.findById(999L);
        assertTrue(trainingOpt.isEmpty(), "Training should not be found with non-existing ID");
    }
    
    @Test
    void testSave_newTraining() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        // Get required entities from database
        var trainee = em.find(com.gym.entity.Trainee.class, 1L);
        var trainer = em.find(com.gym.entity.Trainer.class, 1L);
        var trainingType = em.find(com.gym.entity.TrainingType.class, 1L);
        
        assertNotNull(trainee, "Trainee should exist");
        assertNotNull(trainer, "Trainer should exist");
        assertNotNull(trainingType, "Training type should exist");
        
        Training newTraining = Training.builder()
            .name("New Workout Session")
            .type(trainingType)
            .duration(45)
            .date(LocalDate.of(2025, 2, 1))
            .trainer(trainer)
            .trainee(trainee)
            .build();
            
        trainingRepository.save(newTraining);
        tx.commit();
        
        // Verify the training was saved
        EntityTransaction queryTx = em.getTransaction();
        queryTx.begin();
        Long count = em.createQuery("SELECT COUNT(t) FROM Training t WHERE t.name = :name", Long.class)
            .setParameter("name", "New Workout Session")
            .getSingleResult();
        queryTx.commit();
        
        assertEquals(1L, count, "New training should be persisted");
    }
    
    @Test
    @Order(1)
    void testSave_updateExistingTraining() {
        Optional<Training> existingTraining = trainingRepository.findById(1L);
        assertTrue(existingTraining.isPresent(), "Training should exist from import.sql");
        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        Training training = existingTraining.get();
        String originalName = training.getName();
        Integer originalDuration = training.getDuration();
        
        training.setName("Updated Morning Workout");
        training.setDuration(90);
        trainingRepository.save(training);
        tx.commit();
        
        Optional<Training> updatedTraining = trainingRepository.findById(1L);
        assertTrue(updatedTraining.isPresent(), "Updated training should exist");
        assertEquals("Updated Morning Workout", updatedTraining.get().getName());
        assertEquals(90, updatedTraining.get().getDuration());
        assertNotEquals(originalName, updatedTraining.get().getName());
        assertNotEquals(originalDuration, updatedTraining.get().getDuration());
    }
    
    @Test
    void testFindTraineeTrainings_withoutFilters() {
        List<Training> trainings = trainingRepository.findTraineeTrainings("trainee_user", null, null, null, null);
        
        assertNotNull(trainings, "Trainings list should not be null");
        assertFalse(trainings.isEmpty(), "Should find trainings for existing trainee");
        
        // Debug output
        System.out.println("Found " + trainings.size() + " trainings for trainee_user");
        trainings.forEach(training -> 
            System.out.println("Training: " + training.getName() + ", Date: " + training.getDate()));
    }
    
    @Test
    void testFindTraineeTrainings_withDateFilter() {
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 1, 15);
        
        List<Training> trainings = trainingRepository.findTraineeTrainings("trainee_user", fromDate, toDate, null, null);
        
        assertNotNull(trainings, "Trainings list should not be null");
    }
    
    @Test
    void testFindTraineeTrainings_withTrainerNameFilter() {
        List<Training> trainings = trainingRepository.findTraineeTrainings("trainee_user", null, null, "Jane", null);
        
        assertNotNull(trainings, "Trainings list should not be null");
    }
    
    
    @Test
    void testFindTraineeTrainings_nonExistingTrainee() {
        List<Training> trainings = trainingRepository.findTraineeTrainings("non_existing_trainee", null, null, null, null);
        
        assertNotNull(trainings, "Trainings list should not be null");
        assertTrue(trainings.isEmpty(), "Should return empty list for non-existing trainee");
    }
    
    @Test
    void testFindTrainerTrainings_nonExistingTrainer() {
        List<Training> trainings = trainingRepository.findTrainerTrainings("non_existing_trainer", null, null, null);
        
        assertNotNull(trainings, "Trainings list should not be null");
        assertTrue(trainings.isEmpty(), "Should return empty list for non-existing trainer");
    }
    
    @Test
    void testDelete_existingTraining() {
        // Create a training for safe deletion
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        var trainee = em.find(com.gym.entity.Trainee.class, 1L);
        var trainer = em.find(com.gym.entity.Trainer.class, 1L);
        var trainingType = em.find(com.gym.entity.TrainingType.class, 2L);
        
        Training trainingToDelete = Training.builder()
            .name("Training to Delete")
            .type(trainingType)
            .duration(30)
            .date(LocalDate.of(2025, 3, 1))
            .trainer(trainer)
            .trainee(trainee)
            .build();
            
        trainingRepository.save(trainingToDelete);
        tx.commit();
        
        // Verify it exists
        tx = em.getTransaction();
        tx.begin();
        Long countBefore = em.createQuery("SELECT COUNT(t) FROM Training t WHERE t.name = :name", Long.class)
            .setParameter("name", "Training to Delete")
            .getSingleResult();
        tx.commit();
        assertEquals(1L, countBefore, "Training should exist before deletion");
        
        // Delete it
        tx = em.getTransaction();
        tx.begin();
        trainingRepository.delete(trainingToDelete);
        tx.commit();
        
        // Verify deletion
        tx = em.getTransaction();
        tx.begin();
        Long countAfter = em.createQuery("SELECT COUNT(t) FROM Training t WHERE t.name = :name", Long.class)
            .setParameter("name", "Training to Delete")
            .getSingleResult();
        tx.commit();
        assertEquals(0L, countAfter, "Training should be deleted");
    }
}