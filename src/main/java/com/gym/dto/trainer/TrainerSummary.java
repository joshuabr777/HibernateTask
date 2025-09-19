package com.gym.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerSummary {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}