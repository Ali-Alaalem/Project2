package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Room;
import com.project.hospital.repositorys.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(Room room) {
        if (room.getRoomNumber() == null)
            throw new IllegalArgumentException("Room number is required");

        if (room.getRoomTreatmentType() == null)
            throw new IllegalArgumentException("Room treatment type is required");

        if (roomRepository.existsByNumber(room.getRoomNumber())) {
            throw new InformationExistException("Room number already exists");
        }

        if (roomRepository.existsByRoomTreatmentType(room.getRoomTreatmentType())) {
            throw new InformationExistException("There is another room with the same treatment type");
        }

        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(
                () -> new InformationNotFoundException("Room with id " + roomId + " not found")
        );
    }


    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).orElseThrow(
                () -> new InformationNotFoundException("Room with number " + roomNumber + " not found")
        );
    }


    public Room deleteRoomById(Long roomId) {
        Room existingRoom = roomRepository.findById(roomId).orElseThrow(
                () -> new InformationNotFoundException("Room with id " + roomId + " not found")
        );
        roomRepository.delete(existingRoom);
        return existingRoom;
    }

    public Room updateRoom(Long roomId, Room roomUpdates) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new InformationNotFoundException("Room with id " + roomId + " not found"));

        if (roomUpdates.getRoomNumber() == null) {
            throw new IllegalArgumentException("Room number is required");
        }
        if (roomUpdates.getRoomTreatmentType() == null) {
            throw new IllegalArgumentException("Room treatment type is required");
        }

        if (!existingRoom.getRoomNumber().equals(roomUpdates.getRoomNumber())) {
            if (roomRepository.existsByNumber(roomUpdates.getRoomNumber())) {
                throw new InformationExistException("Room number already exists");
            }
        }

        boolean treatmentTypeChanged = !existingRoom.getRoomTreatmentType().equals(roomUpdates.getRoomTreatmentType());
        if (treatmentTypeChanged) {
            if (roomRepository.existsByRoomTreatmentType(roomUpdates.getRoomTreatmentType())) {
                throw new InformationExistException("There is another room with the same treatment type");
            }
        }

        existingRoom.setRoomNumber(roomUpdates.getRoomNumber());
        existingRoom.setDescription(roomUpdates.getDescription());
        existingRoom.setWorkDaysAndHours(roomUpdates.getWorkDaysAndHours());
        existingRoom.setRoomTreatmentType(roomUpdates.getRoomTreatmentType());

        return roomRepository.save(existingRoom);
    }


}
