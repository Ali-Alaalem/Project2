package com.project.hospital.repositorys;

import com.project.hospital.models.TreatmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreatmentTypeRepository extends JpaRepository<TreatmentType, Long> {
    Optional<TreatmentType> findByType(String type);
    boolean existsByType(String type);

}
