package com.gym.dto.training;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingSummary {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private double trainingDuration;
    private String personName; // trainee or trainer depending on endpoint
}