package com.project.hospital.repositorys;

import com.project.hospital.models.Person;
import com.project.hospital.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUser(Long userId);
}
