package com.project.hospital.repositorys;

import com.project.hospital.models.Room;
import com.project.hospital.models.TreatmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);
    boolean existsByRoomTreatmentType(TreatmentType treatmentType);

    Optional<Room> findByRoomNumber(String roomNumber);
}
