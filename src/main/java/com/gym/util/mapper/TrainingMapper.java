package com.gym.util.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gym.dto.training.AddTrainingRequest;
import com.gym.dto.training.TrainingSummary;
import com.gym.entity.Training;

@Mapper(componentModel = "spring", uses = {TrainerMapper.class, TraineeMapper.class, TrainingTypeMapper.class})
public interface TrainingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee", ignore = true) // set in service based on username
    @Mapping(target = "trainer", ignore = true) // set in service based on username
    @Mapping(target = "type", ignore = true)    // set in service based on trainingTypeName
    @Mapping(source = "trainingDate", target = "date")
    @Mapping(source = "trainingDuration", target = "duration")
    @Mapping(source = "trainingName", target = "name")
    Training toEntity(AddTrainingRequest dto);

    @Mapping(source = "name", target = "trainingName")
    @Mapping(source = "type.name", target = "trainingType")
    @Mapping(source = "duration", target = "trainingDuration")
    @Mapping(source = "date", target = "trainingDate")
    TrainingSummary toSummary(Training training);

    List<TrainingSummary> toSummaryList(List<Training> trainings);
}

