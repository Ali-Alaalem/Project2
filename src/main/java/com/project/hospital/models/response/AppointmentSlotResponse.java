package com.project.hospital.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AppointmentSlotResponse {
    private Long appointmentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long doctorId;
    private boolean booked;
}
