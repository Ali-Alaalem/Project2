package com.project.hospital.repositorys;

import com.project.hospital.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
    String findByToken(String token);
}
