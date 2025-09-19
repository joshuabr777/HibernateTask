package com.gym.dto.trainee;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTraineeTrainersRequest {
    @NotBlank
    private String traineeUsername;

    @NotEmpty
    private List<@NotBlank String> trainerUsernames;
}