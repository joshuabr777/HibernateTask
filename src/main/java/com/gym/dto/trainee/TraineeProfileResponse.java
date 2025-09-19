package com.gym.dto.trainee;

import java.time.LocalDate;
import java.util.List;

import com.gym.dto.trainer.TrainerSummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeProfileResponse {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean isActive;
    private List<TrainerSummary> trainers;
}
