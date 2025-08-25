package com.gym.service;

import java.util.List;
import java.util.Optional;

import com.gym.entity.User;

public interface UserService {

    User createUser(String firstName, String lastName);
    
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User updateUser(User user);

    boolean activateUser(String username);

    boolean deactivateUser(String username);

    void deleteUser(User user);

    List<User> findAll();
}
