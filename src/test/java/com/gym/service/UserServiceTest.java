package com.gym.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.gym.entity.User;
import com.gym.repository.UserRepository;
import com.gym.service.impl.UserServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeAll
    void init() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUser_success() {
        when(userRepository.findAll()).thenReturn(List.of()); // mock empty list
        doNothing().when(userRepository).save(any(User.class)); // void save

        User result = userService.createUser("Alice", "Smith");

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertTrue(result.isActive());
    }

    @Test
    void testActivateUser_success() {
        User user = User.builder().username("user1").isActive(false).build();
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).save(any(User.class));

        boolean result = userService.activateUser("user1");

        assertTrue(result);
        assertTrue(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void testDeactivateUser_success() {
        User user = User.builder().username("user1").isActive(true).build();
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).save(any(User.class));

        boolean result = userService.deactivateUser("user1");

        assertTrue(result);
        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void testActivateUser_userAlreadyActive() {
        User user = User.builder().username("user1").isActive(true).build();
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        boolean result = userService.activateUser("user1");

        assertFalse(result); // already active
    }

    @Test
    void testDeactivateUser_userNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        boolean result = userService.deactivateUser("missing");

        assertFalse(result); // user not found
    }
}