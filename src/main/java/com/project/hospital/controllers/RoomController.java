package com.project.hospital.controllers;


import com.project.hospital.models.Room;
import com.project.hospital.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @GetMapping("/")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable("roomId") Long roomId) {
        return roomService.getRoomById(roomId);
    }

    @DeleteMapping("/{roomId}")
    public Room deleteRoom(@PathVariable("roomId") Long roomId) {
        return roomService.deleteRoomById(roomId);
    }

    @PutMapping("/{roomId}")
    public Room updateRoom(@PathVariable("roomId") Long roomId, @RequestBody Room room) {
        return roomService.updateRoom(roomId, room);
    }

}
