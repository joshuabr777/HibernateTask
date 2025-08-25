package com.gym.util;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;

public class TestUtils {
    public static void injectEntityManager(Object repo, EntityManager em) {
        try {
            Field field = repo.getClass().getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repo, em);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject EntityManager", e);
        }
    }
}