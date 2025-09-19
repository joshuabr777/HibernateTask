package com.gym.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.dto.trainee.TraineeRegistrationRequest;
import com.gym.dto.trainee.TraineeRegistrationResponse;
import com.gym.entity.Trainee;
import com.gym.facade.GymFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;

@Api(tags = "Trainee API")
@RestController
@RequestMapping("/api/v1")
@Validated
public class TraineeController {
    private final GymFacade facade;
    private static final Logger log = LoggerFactory.getLogger(TraineeController.class.getName());

    @Autowired
    public TraineeController(GymFacade facade) {
        this.facade = facade;
    }
    
    @ApiOperation("Register Trainee")
    @PostMapping("/trainees")
    public ResponseEntity<TraineeRegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest request) {
 //TODO: Implement the method
            return ResponseEntity.ok().body(null);
    }
}
