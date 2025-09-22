package com.gym.util.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gym.dto.trainer.TrainerProfileResponse;
import com.gym.dto.trainer.TrainerRegistrationRequest;
import com.gym.dto.trainer.TrainerRegistrationResponse;
import com.gym.dto.trainer.TrainerSummary;
import com.gym.dto.trainer.UpdateTrainerProfileRequest;
import com.gym.entity.Trainer;

@Mapper(componentModel = "spring", uses = TraineeMapper.class)
public interface TrainerMapper {

    // --- DTO -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // user is created separately
    @Mapping(target = "trainees", ignore = true)
    @Mapping(source = "specialization", target = "specialization.name")
    Trainer toEntity(TrainerRegistrationRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "specialization", ignore = true) // specialization is not updated here
    @Mapping(source = "lastName", target = "user.lastName")
    @Mapping(source = "firstName", target = "user.firstName")
    @Mapping(source = "isActive", target = "user.isActive")
    Trainer toEntity(UpdateTrainerProfileRequest dto);

    // --- Entity -> DTO ---
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "specialization.name", target = "specialization")
    @Mapping(source = "trainees", target = "trainees")
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.username", target = "username")
    TrainerRegistrationResponse toRegistrationResponse(Trainer trainer);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "specialization.name", target = "specialization")
    TrainerSummary toSummary(Trainer trainer);

    List<TrainerSummary> toSummaryList(List<Trainer> trainers);
}
