package com.project.hospital.repositorys;

import com.project.hospital.models.Role;
import com.project.hospital.models.TreatmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.hospital.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAddress(String emailAddress);

    Optional<User> findByEmailAddress(String emailAddress);

    List<User> findByRole(Role role);

    List<User> findByTreatmentType(TreatmentType treatmentType);
}
