package com.gym.repository;

import java.util.Optional;

import com.gym.entity.Trainer;

public interface TrainerRepository {
    void save(Trainer trainer);
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    void delete(Trainer trainer);
}
