package com.project.hospital.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomAvailabilityResponse {
    private Long roomId;
    private String roomNumber;
    private List<AppointmentSlotResponse> appointments;
}
