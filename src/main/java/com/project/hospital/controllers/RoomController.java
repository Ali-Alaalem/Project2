package com.project.hospital.controllers;


import com.project.hospital.models.Room;
import com.project.hospital.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
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

}
