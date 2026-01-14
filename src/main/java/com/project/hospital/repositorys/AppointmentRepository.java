package com.project.hospital.repositorys;

import com.project.hospital.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Use ID-based queries (more efficient)
    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByRoomId(Long roomId);

    List<Appointment> findByBookingId(Long bookingId);

    boolean existsByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    boolean existsByRoomIdAndStartTimeBetween(Long roomId, LocalDateTime start, LocalDateTime end);

    // Get all appointments for users with DOCTOR role
    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.role r WHERE r.name = 'DOCTOR'")
    List<Appointment> findAllDoctorAppointments();

    // Get appointments by doctor ID with role verification
    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.role r WHERE d.id = :doctorId AND r.name = 'DOCTOR'")
    List<Appointment> findByDoctorIdWithRoleCheck(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor d LEFT JOIN FETCH a.booking b WHERE a.room.id = :roomId AND a.startTime BETWEEN :start AND :end ORDER BY a.startTime")
    List<Appointment> findRoomAppointmentsWithDoctorAndBooking(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}