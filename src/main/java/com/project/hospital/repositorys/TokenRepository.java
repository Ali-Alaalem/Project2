package com.project.hospital.repositorys;

import com.project.hospital.models.Token;
import com.project.hospital.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);
    void deleteByUser(User user);
}
