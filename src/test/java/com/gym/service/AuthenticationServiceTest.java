package com.gym.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.gym.entity.User;
import com.gym.repository.UserRepository;
import com.gym.service.impl.AuthenticationServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationServiceTest {

    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    @BeforeAll
    void init() {
        userRepository = Mockito.mock(UserRepository.class);
        authenticationService = new AuthenticationServiceImpl(userRepository);
    }

    @Test
    void testAuthenticate_success() {
        User user = User.builder()
                .username("user1")
                .password("pass")
                .isActive(true)
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.authenticate("user1", "pass");

        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
    }

    @Test
    void testAuthenticate_failWrongPassword() {
        User user = User.builder()
                .username("user1")
                .password("pass")
                .isActive(true)
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.authenticate("user1", "wrong");

        assertTrue(result.isEmpty());
    }

    @Test
    void testAuthenticate_failInactiveUser() {
        User user = User.builder()
                .username("user1")
                .password("pass")
                .isActive(false)
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<User> result = authenticationService.authenticate("user1", "pass");

        assertTrue(result.isEmpty(), "Inactive user should not authenticate");
    }

    @Test
    void testChangePassword_success() {
        User user = User.builder()
                .username("user1")
                .password("oldPass")
                .isActive(true)
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).save(any(User.class));

        boolean success = authenticationService.changePassword("user1", "oldPass", "newPass");

        assertTrue(success);
        assertEquals("newPass", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_failWrongPassword() {
        User user = User.builder()
                .username("user1")
                .password("oldPass")
                .isActive(true)
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        boolean success = authenticationService.changePassword("user1", "wrongPass", "newPass");

        assertFalse(success, "Password change should fail with wrong password");
        verify(userRepository, never()).save(any(User.class));
    }
}