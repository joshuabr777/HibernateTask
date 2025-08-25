package com.gym.repository.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.gym.entity.TrainingType;
import com.gym.repository.TrainingTypeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    
    private static final Logger log = LoggerFactory.getLogger(TrainingTypeRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TrainingType> findById(Long id) {
        try {
            log.debug("Finding training type by id: {}", id);
            Optional<TrainingType> trainingType = Optional.ofNullable(entityManager.find(TrainingType.class, id));
            if (trainingType.isPresent()) {
                log.debug("Found training type with id: {}", id);
            } else {
                log.debug("No training type found with id: {}", id);
            }
            return trainingType;
        } catch (Exception e) {
            log.error("Error finding training type by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        try {
            log.debug("Finding training type by name: {}", name);
            String jpql = "SELECT tt FROM TrainingType tt WHERE tt.name = :name";
            Optional<TrainingType> trainingType = entityManager.createQuery(jpql, TrainingType.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst();
            
            if (trainingType.isPresent()) {
                log.debug("Found training type with name: {}", name);
            } else {
                log.debug("No training type found with name: {}", name);
            }
            return trainingType;
        } catch (Exception e) {
            log.error("Error finding training type by name: {}", name, e);
            return Optional.empty();
        }
    }

    @Override
    public List<TrainingType> findAll() {
        try {
            log.debug("Finding all training types");
            String jpql = "SELECT tt FROM TrainingType tt ORDER BY tt.name";
            List<TrainingType> trainingTypes = entityManager.createQuery(jpql, TrainingType.class)
                    .getResultList();
            
            log.debug("Found {} training types", trainingTypes.size());
            return trainingTypes;
        } catch (Exception e) {
            log.error("Error finding all training types", e);
            return List.of();
        }
    }
}
