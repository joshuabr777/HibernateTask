package com.gym.repository;

import com.gym.entity.User;
import com.gym.repository.impl.UserRepositoryImpl;
import com.gym.util.TestUtils;

import org.junit.jupiter.api.*;
import jakarta.persistence.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private UserRepository userRepository;
    
    @BeforeAll
    void init() {
        // Use the persistence unit name from persistence.xml
        // The import.sql file will automatically load test data
        emf = Persistence.createEntityManagerFactory("test-gym-app");
    }
    
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        userRepository = new UserRepositoryImpl();
        // manually inject EntityManager
        TestUtils.injectEntityManager(userRepository, em);
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
    void testFindByUsername_existingUser() {
        Optional<User> userOpt = userRepository.findByUsername("trainee_user");
        assertTrue(userOpt.isPresent(), "User should be found");
        assertEquals("Johnny", userOpt.get().getFirstName()); // assert the updated name
    }
    
    @Test
    void testFindByUsername_nonExistingUser() {
        Optional<User> userOpt = userRepository.findByUsername("does_not_exist");
        assertTrue(userOpt.isEmpty(), "User should not be found");
    }
    
    @Test
    void testSave_newUser() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // Don't specify ID, let Hibernate generate it and use builder pattern
        User newUser = User.builder()
            .firstName("Alice")
            .lastName("Wonderland")
            .username("alice_user")
            .password("secret")
            .isActive(true)
            .build();
            
        userRepository.save(newUser);
        tx.commit();
        
        Optional<User> userOpt = userRepository.findByUsername("alice_user");
        assertTrue(userOpt.isPresent(), "New user should be persisted");
        assertEquals("Alice", userOpt.get().getFirstName());
    }
    
    @Test
    @Order(1)
    void testSave_updateExistingUser() {
        // Find user by ID
        Optional<User> existingUser = userRepository.findById(1L);
        assertTrue(existingUser.isPresent(), "Test user should exist from import.sql");
        
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        User user = existingUser.get();
        String originalFirstName = user.getFirstName();
        user.setFirstName("Johnny"); // Update the name
        userRepository.save(user);
        tx.commit();
        
        // Verify the update by ID
        Optional<User> updatedUser = userRepository.findById(1L);
        assertTrue(updatedUser.isPresent(), "Updated user should exist");
        assertEquals("Johnny", updatedUser.get().getFirstName());
        assertNotEquals(originalFirstName, updatedUser.get().getFirstName());
    }
    
    @Test
    void testFindById_existingUser() {
        Optional<User> foundUser = userRepository.findById(1L);
        
        assertTrue(foundUser.isPresent(), "User should be found by ID");
        assertEquals("Johnny", foundUser.get().getFirstName()); // assert the updated name
        assertEquals("trainee_user", foundUser.get().getUsername());
    }
    
    @Test
    void testFindById_nonExistingUser() {
        Optional<User> userOpt = userRepository.findById(999L);
        assertTrue(userOpt.isEmpty(), "User should not be found with non-existing ID");
    }
    
    @Test
    void testFindAll_checkInitialData() {
        var allUsers = userRepository.findAll();
        System.out.println("Found " + allUsers.size() + " users in database");
        
        allUsers.forEach(user -> 
            System.out.println("User: " + user.getUsername() + ", Name: " + 
                             user.getFirstName() + " " + user.getLastName()));
        
        assertFalse(allUsers.isEmpty(), "Should find at least some users from import.sql");
    }
    
    @Test
    void testDelete_existingUser() {
        // Create a user that doesn't have dependent records e.g. trainee or trainer
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        User standAloneUser = User.builder()
            .firstName("Bob")
            .lastName("Standalone")
            .username("bob_standalone")
            .password("password")
            .isActive(true)
            .build();
            
        userRepository.save(standAloneUser);
        tx.commit();
        
        // Verify user exists
        Optional<User> userOpt = userRepository.findByUsername("bob_standalone");
        assertTrue(userOpt.isPresent(), "User must exist before delete");
        
        // Now delete it
        tx = em.getTransaction();
        tx.begin();
        userRepository.delete(userOpt.get());
        tx.commit();
        
        // Verify deletion
        Optional<User> deletedUser = userRepository.findByUsername("bob_standalone");
        assertTrue(deletedUser.isEmpty(), "User should be deleted");
    }
}