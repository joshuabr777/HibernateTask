package com.gym;

import com.gym.config.AppConfig;
import com.gym.entity.Trainee;
import com.gym.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            GymFacade facade = context.getBean(GymFacade.class);

            // Create a new trainee

            Trainee createdTrainee = facade.createTrainee("John", "Doe", LocalDate.of(1995, 5, 15), "123 Main St");
            logger.info("Created trainee with username: {}", createdTrainee.getUser().getUsername());

            // Authenticate trainee
            var authTrainee = facade.authenticateTrainee(createdTrainee.getUser().getUsername(), 
                                                         createdTrainee.getUser().getPassword());
            if (authTrainee.isPresent()) {
                logger.info("Authentication successful for trainee: {}", createdTrainee.getUser().getUsername());
            }

            // Create a new trainer
            var createdTrainer = facade.createTrainer("Alice", "Smith", "Strength");
            logger.info("Created trainer with username: {}", createdTrainer.getUser().getUsername());

            // Add a training session
            var training = facade.addTraining(
                    createdTrainee.getUser().getUsername(),
                    createdTrainer.getUser().getUsername(),
                    "Morning Strength",
                    createdTrainer.getSpecialization().getId(),
                    LocalDate.now().plusDays(1),
                    60,
                    createdTrainee.getUser().getUsername(),
                    createdTrainee.getUser().getPassword()
            );
            logger.info("Added training: {} for trainee: {}", training.getName(), createdTrainee.getUser().getUsername());

            // List trainee trainings
            var trainings = facade.getTraineeTrainings(
                    createdTrainee.getUser().getUsername(),
                    createdTrainee.getUser().getPassword(),
                    null, null, null, null
            );
            logger.info("Trainee {} has {} trainings", createdTrainee.getUser().getUsername(), trainings.size());

            // Timestamp log
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.info("✅ Application run successful [{}]", timestamp);
        } catch (Exception e) {
            logger.error("Error during application run", e);
        }
    }
}
