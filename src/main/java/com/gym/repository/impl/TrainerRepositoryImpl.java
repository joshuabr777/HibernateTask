package com.gym.repository.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.gym.entity.Trainer;
import com.gym.repository.TrainerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    
    private static final Logger log = LoggerFactory.getLogger(TrainerRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Trainer trainer) {
        try {
            if (trainer.getId() == null) {
                log.debug("Persisting new trainer with username: {}", trainer.getUser().getUsername());
                entityManager.persist(trainer);
                log.info("Successfully created new trainer with username: {}", trainer.getUser().getUsername());
            } else {
                log.debug("Updating existing trainer with id: {}", trainer.getId());
                entityManager.merge(trainer);
                log.info("Successfully updated trainer with username: {}", trainer.getUser().getUsername());
            }
        } catch (Exception e) {
            log.error("Error saving trainer with username: {}", trainer.getUser().getUsername());
            throw e; // Rethrowing the exception for transaction rollback
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        try {
            log.debug("Finding trainer by id: {}", id);
            Optional<Trainer> trainer = Optional.ofNullable(entityManager.find(Trainer.class, id));
            if (trainer.isPresent()) {
                log.debug("Found trainer with id: {}", id);
            } else {
                log.debug("No trainer found with id: {}", id);
            }
            return trainer;
        } catch (Exception e) {
            log.error("Error finding trainer by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            log.debug("Finding trainer by username: {}", username);
            String jpql = "SELECT t FROM Trainer t WHERE t.user.username = :username";
            Optional<Trainer> trainer = entityManager.createQuery(jpql, Trainer.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
            
            if (trainer.isPresent()) {
                log.debug("Found trainer with username: {}", username);
            } else {
                log.debug("No trainer found with username: {}", username);
            }
            return trainer;
        } catch (Exception e) {
            log.error("Error finding trainer by username: {}", username, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(Trainer trainer) {
        try {
            log.debug("Deleting trainer with username: {}", trainer.getUser().getUsername());
            Trainer managedTrainer = entityManager.contains(trainer) ? trainer : entityManager.merge(trainer);
            entityManager.remove(managedTrainer);
            log.info("Successfully deleted trainer with username: {}", trainer.getUser().getUsername());
        } catch (Exception e) {
            log.error("Error deleting trainer with username: {}", trainer.getUser().getUsername());
            throw e;
        }
    }
}
