package com.gym.dto.training;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingFilterRequest {
    @NotBlank
    private String username;

    private LocalDate periodFrom;
    private LocalDate periodTo;
    private String traineeName;
    private String trainerName;
    private String trainingType;
}
