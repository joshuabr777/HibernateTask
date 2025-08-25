package com.gym.repository.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.gym.entity.Training;
import com.gym.repository.TrainingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    
    private static final Logger log = LoggerFactory.getLogger(TrainingRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Training training) {
        try {
            if (training.getId() == null) {
                log.debug("Persisting new training: {}", training.getName());
                entityManager.persist(training);
                log.info("Successfully created new training: {}", training.getName());
            } else {
                log.debug("Updating existing training with id: {}", training.getId());
                entityManager.merge(training);
                log.info("Successfully updated training: {}", training.getName());
            }
        } catch (Exception e) {
            log.error("Error saving training: {}", training.getName());
            throw e; // Rethrowing the exception for transaction rollback
        }
    }

    @Override
    public Optional<Training> findById(Long id) {
        try {
            log.debug("Finding training by id: {}", id);
            Optional<Training> training = Optional.ofNullable(entityManager.find(Training.class, id));
            if (training.isPresent()) {
                log.debug("Found training with id: {}", id);
            } else {
                log.debug("No training found with id: {}", id);
            }
            return training;
        } catch (Exception e) {
            log.error("Error finding training by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        try {
            log.debug("Finding trainings for trainee: {} with criteria", traineeUsername);
            
            StringBuilder jpql = new StringBuilder("""
                SELECT t FROM Training t 
                JOIN t.trainee te 
                JOIN te.user tu 
                JOIN t.trainer tr 
                JOIN tr.user tru 
                JOIN t.type tt 
                WHERE tu.username = :traineeUsername
                """);

            if (fromDate != null) {
                jpql.append(" AND t.date >= :fromDate");
            }
            if (toDate != null) {
                jpql.append(" AND t.date <= :toDate");
            }
            if (trainerName != null && !trainerName.trim().isEmpty()) {
                jpql.append(" AND (tru.firstName LIKE :trainerName OR tru.lastName LIKE :trainerName)");
            }
            if (trainingTypeName != null && !trainingTypeName.trim().isEmpty()) {
                jpql.append(" AND tt.name = :trainingTypeName");
            }
            
            jpql.append(" ORDER BY t.date DESC");

            TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);
            query.setParameter("traineeUsername", traineeUsername);
            
            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                query.setParameter("toDate", toDate);
            }
            if (trainerName != null && !trainerName.trim().isEmpty()) {
                query.setParameter("trainerName", "%" + trainerName.trim() + "%");
            }
            if (trainingTypeName != null && !trainingTypeName.trim().isEmpty()) {
                query.setParameter("trainingTypeName", trainingTypeName);
            }

            List<Training> trainings = query.getResultList();
            log.debug("Found {} trainings for trainee: {}", trainings.size(), traineeUsername);
            return trainings;
        } catch (Exception e) {
            log.error("Error finding trainings for trainee: {}", traineeUsername, e);
            return List.of();
        }
    }

    @Override
    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        try {
            log.debug("Finding trainings for trainer: {} with criteria", trainerUsername);
            
            StringBuilder jpql = new StringBuilder("""
                SELECT t FROM Training t 
                JOIN t.trainer tr 
                JOIN tr.user tru 
                JOIN t.trainee te 
                JOIN te.user teu 
                WHERE tru.username = :trainerUsername
                """);

            if (fromDate != null) {
                jpql.append(" AND t.date >= :fromDate");
            }
            if (toDate != null) {
                jpql.append(" AND t.date <= :toDate");
            }
            if (traineeName != null && !traineeName.trim().isEmpty()) {
                jpql.append(" AND (teu.firstName LIKE :traineeName OR teu.lastName LIKE :traineeName)");
            }
            
            jpql.append(" ORDER BY t.date DESC");

            TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);
            query.setParameter("trainerUsername", trainerUsername);
            
            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                query.setParameter("toDate", toDate);
            }
            if (traineeName != null && !traineeName.trim().isEmpty()) {
                query.setParameter("traineeName", "%" + traineeName.trim() + "%");
            }

            List<Training> trainings = query.getResultList();
            log.debug("Found {} trainings for trainer: {}", trainings.size(), trainerUsername);
            return trainings;
        } catch (Exception e) {
            log.error("Error finding trainings for trainer: {}", trainerUsername);
            return List.of();
        }
    }

    @Override
    public void delete(Training training) {
        try {
            log.debug("Deleting training: {}", training.getName());
            Training managedTraining = entityManager.contains(training) ? training : entityManager.merge(training);
            entityManager.remove(managedTraining);
            log.info("Successfully deleted training: {}", training.getName());
        } catch (Exception e) {
            log.error("Error deleting training: {}", training.getName());
            throw e; // Rethrowing the exception for transaction rollback
        }
    }
}

