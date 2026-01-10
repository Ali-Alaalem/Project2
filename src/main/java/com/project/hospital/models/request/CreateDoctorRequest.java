package com.project.hospital.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashMap;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDoctorRequest {
    private String fullName;
    private String emailAddress;
    private String password;
    private Long treatmentTypeId;
    private HashMap<String, HashMap<String, LocalTime>> workDaysAndHours = new HashMap<>();
}
