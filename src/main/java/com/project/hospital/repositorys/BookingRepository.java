package com.project.hospital.repositorys;


import com.project.hospital.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPatientId(Long patientId);

    boolean existsByPatientId(Long patientId);

    List<Booking> findByPatientIdAndBookedAtBetween(
            Long patientId,
            LocalDateTime start,
            LocalDateTime end
    );


}
