package com.gym.util.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gym.dto.training_type.TrainingTypeResponse;
import com.gym.entity.TrainingType;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    @Mapping(source = "name", target = "trainingType")
    @Mapping(source = "id", target = "trainingTypeId")
    TrainingTypeResponse toResponse(TrainingType type);

    List<TrainingTypeResponse> toResponseList(List<TrainingType> types);
}

