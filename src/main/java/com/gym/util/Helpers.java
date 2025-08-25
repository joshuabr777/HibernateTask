package com.gym.util;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helpers {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(Helpers.class);

    private Helpers() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generatePassword() {
        String password = generateRandomPassword();
        logger.debug("Password generated: [HIDDEN]");
        return password;
    }

    private static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String generateUsername(String firstName, String lastName, List<String> existingUsernames) {
        String base = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = base;
        int counter = 1;

        Set<String> existingSet = existingUsernames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        while (existingSet.contains(username.toLowerCase())) {
            username = base + counter++;
        }

        logger.debug("Username generated for user: {}.***", firstName);
        return username;
    }
}