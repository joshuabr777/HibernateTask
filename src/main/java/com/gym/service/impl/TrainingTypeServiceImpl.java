package com.gym.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.entity.TrainingType;
import com.gym.repository.TrainingTypeRepository;
import com.gym.service.TrainingTypeService;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService{
    
    private static final Logger log = LoggerFactory.getLogger(TrainingTypeServiceImpl.class);
    
    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    /**
     * Finds training type by ID
     * @param id the training type ID
     * @return Optional<TrainingType>
     */
    @Override
    public Optional<TrainingType> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return trainingTypeRepository.findById(id);
    }
    
    /**
     * Finds training type by name
     * @param name the training type name
     * @return Optional<TrainingType>
     */
    @Override
    public Optional<TrainingType> findByName(String name) {
        if (isBlank(name)) {
            return Optional.empty();
        }
        return trainingTypeRepository.findByName(name.trim());
    }
    
    /**
     * Gets all available training types
     * @return List of all training types
     */
    @Override
    public List<TrainingType> findAll() {
        List<TrainingType> types = trainingTypeRepository.findAll();
        log.debug("Found {} training types", types.size());
        return types;
    }
    
    /**
     * Checks if a training type exists by ID
     * @param id the training type ID
     * @return boolean indicating existence
     */
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            return trainingTypeRepository.findById(id).isPresent();
        } catch (Exception e) {
            log.error("Error checking existence of training type by ID: {}", id, e);
            return false;
        }
    }
    
    /**
     * Checks if a training type exists by name
     * @param name the training type name
     * @return boolean indicating existence
     */
    @Override
    public boolean existsByName(String name) {
        if (isBlank(name)) {
            return false;
        }
        return trainingTypeRepository.findByName(name.trim()).isPresent();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}