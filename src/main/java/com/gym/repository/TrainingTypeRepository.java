package com.gym.repository;

import java.util.List;
import java.util.Optional;

import com.gym.entity.TrainingType;

public interface TrainingTypeRepository {
    Optional<TrainingType> findById(Long id);
    Optional<TrainingType> findByName(String name);
    List<TrainingType> findAll();
}
