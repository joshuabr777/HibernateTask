package com.gym.service;

import java.util.List;
import java.util.Optional;

import com.gym.entity.TrainingType;

public interface TrainingTypeService {
    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByName(String name);

    List<TrainingType> findAll();

    boolean existsById(Long id);

    boolean existsByName(String name);
}
