package com.gym.service;

import java.util.Optional;

import com.gym.entity.User;

public interface AuthenticationService {
    Optional<User> authenticate(String username, String password);
    boolean isUserActiveByUsername(String username);
    boolean changePassword(String username, String oldPassword, String newPassword);
}
