package com.gym.dto.training_type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTypeResponse {
    private String trainingType;
    private Long trainingTypeId;
}
