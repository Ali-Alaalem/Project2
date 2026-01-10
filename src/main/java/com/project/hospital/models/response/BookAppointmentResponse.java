package com.project.hospital.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookAppointmentResponse {
    private Long bookingId;
    private Long appointmentId;
    private LocalDateTime bookedAt;
    private LocalDateTime appointmentStartTime;
    private LocalDateTime appointmentEndTime;
}
