package com.gym.repository.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.gym.entity.User;
import com.gym.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(User user) {
        try {
            if (user.getId() == null) {
                log.debug("Persisting new user with username: {}", user.getUsername());
                entityManager.persist(user);
                log.info("Successfully created new user with username: {}", user.getUsername());
            } else {
                log.debug("Updating existing user with id: {} and username: {}", user.getId(), user.getUsername());
                entityManager.merge(user);
                log.info("Successfully updated user with username: {}", user.getUsername());
            }
        } catch (Exception e) {
            log.error("Error saving user with username: {}", user.getUsername());
            // rethrowing the exception for transaction rollback
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            log.debug("Finding user by id: {}", id);
            Optional<User> user = Optional.ofNullable(entityManager.find(User.class, id));
            if (user.isPresent()) {
                log.debug("Found user with id: {}", id);
            } else {
                log.debug("No user found with id: {}", id);
            }
            return user;
        } catch (Exception e) {
            log.error("Error finding user by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            log.debug("Finding user by username: {}", username);
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            Optional<User> user = entityManager.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
            
            if (user.isPresent()) {
                log.debug("Found user with username: {}", username);
            } else {
                log.debug("No user found with username: {}", username);
            }
            return user;
        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try {
            log.debug("Finding all users");
            String jpql = "SELECT u FROM User u";
            List<User> users = entityManager.createQuery(jpql, User.class).getResultList();
            log.debug("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error finding all users", e);
            return List.of();
        }
    }
    @Override
    @Transactional
    public void delete(User user) {
        try {
            log.debug("Deleting user with username: {}", user.getUsername());
            User managedUser = entityManager.contains(user) ? user : entityManager.merge(user);
            entityManager.remove(managedUser);
            log.info("Successfully deleted user with username: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error deleting user with username: {}", user.getUsername());
            throw e; // Rethrowing the exception for transaction rollback
        }
    }
}