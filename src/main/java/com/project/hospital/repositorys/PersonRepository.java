package com.project.hospital.repositorys;

import com.project.hospital.models.Person;
import com.project.hospital.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByUser(Long userId);
}
