package com.gym.repository;

import java.util.List;
import java.util.Optional;

import com.gym.entity.Trainee;
import com.gym.entity.Trainer;

public interface TraineeRepository {
    void save(Trainee trainee);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    void delete(Trainee trainee);
    List<Trainer> findUnassignedTrainers(String traineeUsername);
}
