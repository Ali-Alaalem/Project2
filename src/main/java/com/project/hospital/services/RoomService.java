package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Room;
import com.project.hospital.models.TreatmentType;
import com.project.hospital.repositorys.RoomRepository;
import com.project.hospital.repositorys.TreatmentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    public RoomService(RoomRepository roomRepository, TreatmentTypeRepository treatmentTypeRepository) {
        this.roomRepository = roomRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    public Room createRoom(Room room) {
        if (room.getRoomNumber() == null)
            throw new IllegalArgumentException("Room number is required");

        if (room.getRoomTreatmentType() == null)
            throw new IllegalArgumentException("Room treatment type is required");

        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
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

//    public Room updateRoom(Long roomId, Room roomUpdates) {
//        Room existingRoom = roomRepository.findById(roomId)
//                .orElseThrow(() -> new InformationNotFoundException("Room with id " + roomId + " not found"));
//
//        if (roomUpdates.getRoomNumber() == null) {
//            throw new IllegalArgumentException("Room number is required");
//        }
//        if (roomUpdates.getRoomTreatmentType() == null) {
//            throw new IllegalArgumentException("Room treatment type is required");
//        }
//
//        if (!existingRoom.getRoomNumber().equals(roomUpdates.getRoomNumber())) {
//            if (roomRepository.existsByRoomNumber(roomUpdates.getRoomNumber())) {
//                throw new InformationExistException("Room number already exists");
//            }
//        }
//
//        boolean treatmentTypeChanged = !existingRoom.getRoomTreatmentType().equals(roomUpdates.getRoomTreatmentType());
//        if (treatmentTypeChanged) {
//            if (roomRepository.existsByRoomTreatmentType(roomUpdates.getRoomTreatmentType())) {
//                throw new InformationExistException("There is another room with the same treatment type");
//            }
//        }
//
//        existingRoom.setRoomNumber(roomUpdates.getRoomNumber());
//        existingRoom.setDescription(roomUpdates.getDescription());
//        existingRoom.setWorkDaysAndHours(roomUpdates.getWorkDaysAndHours());
//        existingRoom.setRoomTreatmentType(roomUpdates.getRoomTreatmentType());
//
//        return roomRepository.save(existingRoom);
//    }


    public Room updateRoom(Long roomId, Room roomUpdates) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new InformationNotFoundException("Room with id " + roomId + " not found"));

        // Only update roomNumber if provided and different
        if (roomUpdates.getRoomNumber() != null && !roomUpdates.getRoomNumber().isBlank()) {
            if (!existingRoom.getRoomNumber().equals(roomUpdates.getRoomNumber())) {
                if (roomRepository.existsByRoomNumber(roomUpdates.getRoomNumber())) {
                    throw new InformationExistException("Room number already exists");
                }
                existingRoom.setRoomNumber(roomUpdates.getRoomNumber());
            }
        }

        // Only update description if provided
        if (roomUpdates.getDescription() != null) {
            existingRoom.setDescription(roomUpdates.getDescription());
        }

        // Only update workDaysAndHours if provided
        if (roomUpdates.getWorkDaysAndHours() != null) {
            existingRoom.setWorkDaysAndHours(roomUpdates.getWorkDaysAndHours());
        }

        // Only update treatment type if provided
        if (roomUpdates.getRoomTreatmentType() != null) {
            // Important: we only get the ID from the sent object
            TreatmentType sentType = roomUpdates.getRoomTreatmentType();
            if (sentType.getId() == null) {
                throw new IllegalArgumentException("Treatment type ID must be provided if updating treatment type");
            }

            // Fetch the actual managed entity from DB
            TreatmentType actualType = treatmentTypeRepository.findById(sentType.getId())
                    .orElseThrow(() -> new InformationNotFoundException("Treatment type not found"));

            // Check if it's actually changing
            if (existingRoom.getRoomTreatmentType() == null ||
                    !existingRoom.getRoomTreatmentType().getId().equals(actualType.getId())) {

                if (roomRepository.existsByRoomTreatmentType(actualType)) {
                    throw new InformationExistException("Another room already uses this treatment type");
                }
                existingRoom.setRoomTreatmentType(actualType);
            }
        }

        return roomRepository.save(existingRoom);
    }


}
