package com.gym.dto.trainer;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotAssignedTrainersResponse {
    private List<TrainerSummary> trainers;
}

