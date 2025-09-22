package com.gym.util.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gym.dto.trainee.TraineeProfileResponse;
import com.gym.dto.trainee.TraineeRegistrationRequest;
import com.gym.dto.trainee.TraineeRegistrationResponse;
import com.gym.dto.trainee.TraineeSummary;
import com.gym.dto.trainee.UpdateTraineeProfileRequest;
import com.gym.entity.Trainee;

@Mapper(componentModel = "spring", uses = {TrainerMapper.class})
public interface TraineeMapper {

    // --- DTO -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toEntity(TraineeRegistrationRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toEntity(UpdateTraineeProfileRequest dto);

    // --- Entity -> DTO ---
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainers", target = "trainers")
    TraineeProfileResponse toProfileResponse(Trainee trainee);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    TraineeRegistrationResponse toRegistrationResponse(Trainee trainee);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    TraineeSummary toSummary(Trainee trainee);

    List<TraineeSummary> toSummaryList(List<Trainee> trainees);
}

