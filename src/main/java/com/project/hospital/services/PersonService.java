package com.project.hospital.services;

import com.project.hospital.repositorys.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private PersonRepository personRepository;

    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    
}
