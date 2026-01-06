package com.project.hospital.repositorys;


import com.project.hospital.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByAction(String action);

    boolean existsByAction(String action);
}
