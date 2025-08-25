package com.gym.repository.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;
import com.gym.repository.TraineeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    
    private static final Logger log = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Trainee trainee) {
        try {
            if (trainee.getId() == null) {
                log.debug("Persisting new trainee with username: {}", trainee.getUser().getUsername());
                entityManager.persist(trainee);
                log.info("Successfully created new trainee with username: {}", trainee.getUser().getUsername());
            } else {
                log.debug("Updating existing trainee with id: {}", trainee.getId());
                entityManager.merge(trainee);
                log.info("Successfully updated trainee with username: {}", trainee.getUser().getUsername());
            }
        } catch (Exception e) {
            log.error("Error saving trainee with username: {}", trainee.getUser().getUsername());
            throw e;
        }
    }
    
    @Override
    public Optional<Trainee> findById(Long id) {
        try {
            log.debug("Finding trainee by id: {}", id);
            Optional<Trainee> trainee = Optional.ofNullable(entityManager.find(Trainee.class, id));
            if (trainee.isPresent()) {
                log.debug("Found trainee with id: {}", id);
            } else {
                log.debug("No trainee found with id: {}", id);
            }
            return trainee;
        } catch (Exception e) {
            log.error("Error finding trainee by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            log.debug("Finding trainee by username: {}", username);
            String jpql = "SELECT t FROM Trainee t WHERE t.user.username = :username";
            Optional<Trainee> trainee = entityManager.createQuery(jpql, Trainee.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
            
            if (trainee.isPresent()) {
                log.debug("Found trainee with username: {}", username);
            } else {
                log.debug("No trainee found with username: {}", username);
            }
            return trainee;
        } catch (Exception e) {
            log.error("Error finding trainee by username: {}", username, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(Trainee trainee) {
        try {
            log.debug("Deleting trainee with username: {}", trainee.getUser().getUsername());
            Trainee managedTrainee = entityManager.contains(trainee) ? trainee : entityManager.merge(trainee);
            entityManager.remove(managedTrainee);
            log.info("Successfully deleted trainee with username: {}", trainee.getUser().getUsername());
        } catch (Exception e) {
            log.error("Error deleting trainee with username: {}", trainee.getUser().getUsername());
            throw e;
        }
    }

    @Override
    public List<Trainer> findUnassignedTrainers(String traineeUsername) {
        try {
            log.debug("Finding unassigned trainers for trainee: {}", traineeUsername);
            String jpql = """
                SELECT tr FROM Trainer tr 
                WHERE tr.user.isActive = true 
                AND tr NOT IN (
                    SELECT t FROM Trainee te 
                    JOIN te.trainers t 
                    JOIN te.user u 
                    WHERE u.username = :username
                )
                """;
            List<Trainer> trainers = entityManager.createQuery(jpql, Trainer.class)
                    .setParameter("username", traineeUsername)
                    .getResultList();
            
            log.debug("Found {} unassigned trainers for trainee: {}", trainers.size(), traineeUsername);
            return trainers;
        } catch (Exception e) {
            log.error("Error finding unassigned trainers for trainee: {}", traineeUsername);
            throw e;
        }
    }
}
