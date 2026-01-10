package com.project.hospital.controllers;


import com.project.hospital.models.Room;
import com.project.hospital.models.Appointment;
import com.project.hospital.models.response.AppointmentSlotResponse;
import com.project.hospital.models.response.RoomAvailabilityResponse;
import com.project.hospital.services.AppointmentService;
import com.project.hospital.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private RoomService roomService;
    private AppointmentService appointmentService;

    @Autowired
    public RoomController(RoomService roomService, AppointmentService appointmentService) {
        this.roomService = roomService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('room:create')")
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('room:view')")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{roomId}")
    @PreAuthorize("hasAuthority('room:view')")
    public Room getRoom(@PathVariable("roomId") Long roomId) {
        return roomService.getRoomById(roomId);
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAuthority('room:delete')")
    public Room deleteRoom(@PathVariable("roomId") Long roomId) {
        return roomService.deleteRoomById(roomId);
    }

    @PutMapping("/{roomId}")
    @PreAuthorize("hasAuthority('room:update')")
    public Room updateRoom(@PathVariable("roomId") Long roomId, @RequestBody Room room) {
        return roomService.updateRoom(roomId, room);
    }

    @GetMapping("/{roomId}/availability")
    @PreAuthorize("hasAuthority('room:view')")
    public RoomAvailabilityResponse getRoomAvailability(
            @PathVariable("roomId") Long roomId,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end
    ) {
        LocalDateTime startDt = (start == null) ? LocalDateTime.now() : LocalDateTime.parse(start);
        LocalDateTime endDt = (end == null) ? startDt.plusDays(30) : LocalDateTime.parse(end);

        Room room = roomService.getRoomById(roomId);
        List<Appointment> appointments = appointmentService.getRoomAppointmentsForAvailability(roomId, startDt, endDt);

        List<AppointmentSlotResponse> slots = appointments.stream()
                .map(a -> new AppointmentSlotResponse(
                        a.getId(),
                        a.getStartTime(),
                        a.getEndTime(),
                        (a.getDoctor() == null) ? null : a.getDoctor().getId(),
                        a.getBooking() != null
                ))
                .collect(Collectors.toList());

        return new RoomAvailabilityResponse(room.getId(), room.getRoomNumber(), slots);
    }

}
