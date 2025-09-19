package com.gym.dto.trainee;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeRegistrationRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
    
    private LocalDate dateOfBirth; // optional

    private String address; // optional
}
