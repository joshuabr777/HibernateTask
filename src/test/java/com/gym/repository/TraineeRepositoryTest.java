package com.gym.repository;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.repository.impl.TraineeRepositoryImpl;
import com.gym.util.TestUtils;

import org.junit.jupiter.api.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraineeRepositoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private TraineeRepository traineeRepository;
    
    @BeforeAll
    void init() {
        emf = Persistence.createEntityManagerFactory("test-gym-app");
    }
    
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        traineeRepository = new TraineeRepositoryImpl();
        TestUtils.injectEntityManager(traineeRepository, em);
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
    void testFindById_existingTrainee() {
        Optional<Trainee> traineeOpt = traineeRepository.findById(1L);
        assertTrue(traineeOpt.isPresent(), "Trainee should be found");
        assertEquals("trainee_user", traineeOpt.get().getUser().getUsername());
        assertEquals("456 Updated Street", traineeOpt.get().getAddress()); // assert updated address
    }
    
    @Test
    void testFindById_nonExistingTrainee() {
        Optional<Trainee> traineeOpt = traineeRepository.findById(999L);
        assertTrue(traineeOpt.isEmpty(), "Trainee should not be found with non-existing ID");
    }
    
    @Test
    void testFindByUsername_existingTrainee() {
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername("trainee_user");
        assertTrue(traineeOpt.isPresent(), "Trainee should be found by username");
        assertEquals("John", traineeOpt.get().getUser().getFirstName());
        assertEquals("456 Updated Street", traineeOpt.get().getAddress()); // assert updated address
    }
    
    @Test
    void testFindByUsername_nonExistingTrainee() {
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername("non_existing_user");
        assertTrue(traineeOpt.isEmpty(), "Trainee should not be found with non-existing username");
    }
    
    @Test
    void testSave_newTrainee() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        // First create a user for the trainee
        var user = em.find(com.gym.entity.User.class, 3L); // Mike Wilson from import.sql
        assertNotNull(user, "User should exist for this test");
        
        Trainee newTrainee = Trainee.builder()
            .user(user)
            .dateOfBirth(LocalDate.of(1992, 3, 15))
            .address("789 Pine St")
            .build();
            
        traineeRepository.save(newTrainee);
        tx.commit();
        
        Optional<Trainee> savedTrainee = traineeRepository.findByUsername("trainer_mike");
        assertTrue(savedTrainee.isPresent(), "New trainee should be persisted");
        assertEquals("789 Pine St", savedTrainee.get().getAddress());
    }
    
    @Test
    @Order(1)
    void testSave_updateExistingTrainee() {
        Optional<Trainee> existingTrainee = traineeRepository.findById(1L);
        assertTrue(existingTrainee.isPresent(), "Trainee should exist from import.sql");
        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        Trainee trainee = existingTrainee.get();
        String originalAddress = trainee.getAddress();
        trainee.setAddress("456 Updated Street");
        traineeRepository.save(trainee);
        tx.commit();
        
        Optional<Trainee> updatedTrainee = traineeRepository.findById(1L);
        assertTrue(updatedTrainee.isPresent(), "Updated trainee should exist");
        assertEquals("456 Updated Street", updatedTrainee.get().getAddress());
        assertNotEquals(originalAddress, updatedTrainee.get().getAddress());
    }
    
    @Test
    void testFindUnassignedTrainers() {
        List<Trainer> unassignedTrainers = traineeRepository.findUnassignedTrainers("trainee_user");
        
        // Based on import.sql, trainee_user (John) should have trainers assigned
        // So we should get trainers that are NOT assigned to this trainee
        assertNotNull(unassignedTrainers, "Unassigned trainers list should not be null");
        
        // Debug output to see what we get
        System.out.println("Found " + unassignedTrainers.size() + " unassigned trainers");
        unassignedTrainers.forEach(trainer -> 
            System.out.println("Unassigned trainer: " + trainer.getUser().getUsername()));
    }
    
    @Test
    void testFindUnassignedTrainers_nonExistingTrainee() {
        List<Trainer> unassignedTrainers = traineeRepository.findUnassignedTrainers("non_existing_trainee");
        
        // Should return all active trainers since the trainee doesn't exist
        assertNotNull(unassignedTrainers, "Unassigned trainers list should not be null");
        assertFalse(unassignedTrainers.isEmpty(), "Should find trainers when trainee doesn't exist");
    }
    
    @Test
    void testDelete_existingTrainee() {
        // Create a trainee without dependencies for safe deletion
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        var user = em.find(com.gym.entity.User.class, 3L); // Mike Wilson from import.sql
        assertNotNull(user, "User should exist for this test");
        
        Trainee traineeToDelete = Trainee.builder()
            .user(user)
            .dateOfBirth(LocalDate.of(1988, 7, 10))
            .address("Delete Me Street")
            .build();
            
        traineeRepository.save(traineeToDelete);
        tx.commit();
        
        // Verify it exists
        Optional<Trainee> savedTrainee = traineeRepository.findByUsername("trainer_mike");
        assertTrue(savedTrainee.isPresent(), "Trainee should exist before deletion");
        
        // Delete it
        tx = em.getTransaction();
        tx.begin();
        traineeRepository.delete(savedTrainee.get());
        tx.commit();
        
        // Verify deletion
        Optional<Trainee> deletedTrainee = traineeRepository.findByUsername("trainer_mike");
        assertTrue(deletedTrainee.isEmpty(), "Trainee should be deleted");
    }
}