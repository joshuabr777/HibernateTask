package com.gym.dto.trainee;

import java.util.List;

import com.gym.dto.trainer.TrainerSummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTraineeTrainersResponse {
    private List<TrainerSummary> trainers;
}