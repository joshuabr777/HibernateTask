package com.gym.repository;

import java.util.List;
import java.util.Optional;

import com.gym.entity.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void delete(User user);
}
