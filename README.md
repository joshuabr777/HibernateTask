# HibernateTask
Gym Management Application
==========================

Overview
--------
This is a simple Gym Management application using Java, JPA (Jakarta Persistence API), Hibernate, and Spring for dependency injection and transaction management. 
It manages Users, Trainees, Trainers, TrainingTypes, and Trainings.

Requirements
------------
- Java 17+
- Maven
- Docker (for running PostgreSQL)
- PostgreSQL JDBC driver

Project Structure
-----------------
- src/main/java/com/gym/
  - entity/       -> JPA entity classes
  - repository/   -> DAO/repository classes
  - service/      -> Service layer
  - facade/       -> Facade for app operations
  - AppRunner.java -> Main application entry point
- src/main/resources/
  - persistence.xml -> JPA configuration
  - data.sql       -> Initial data for training types
- docker-compose.yml -> PostgreSQL container setup

Database
--------
- PostgreSQL 16 (containerized)
- Database: hibernate_db
- User: test
- Password: test1234
- Port: 1234 (mapped from 5432 inside container)

Docker Setup
------------
1. Ensure Docker is installed and running.
2. Open a terminal in the project root (where docker-compose.yml is located).
3. Run:
   
   docker-compose up -d

4. Verify the container is running:

   docker ps

5. Database connection info:

   JDBC URL: jdbc:postgresql://localhost:1234/hibernate_db
   Username: test
   Password: test1234

Running the Application
-----------------------
1. Build the project (if using Maven):
   
   mvn clean install

2. Run the main class:

   java -cp target/classes com.gym.AppRunner

3. The application will:
   - Create initial users, trainees, trainers
   - Authenticate users
   - Add training sessions
   - Print logs of all operations

Initial Data
------------
- The `data.sql` file is automatically loaded on startup.
- It contains predefined TrainingTypes (e.g., Strength, Cardio, Flexibility) to avoid errors when creating trainers or trainings.

Spring & JPA
------------
- Uses Spring @Configuration for beans.
- Uses Spring Data repositories for transaction management.
- EntityManager is injected with @PersistenceContext.
- Transactions are managed with @Transactional annotations.

Notes
-----
- Ensure the database container is running before executing the Java application.
- The schema is auto-created and dropped at startup using


