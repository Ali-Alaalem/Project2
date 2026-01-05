package com.project.hospital.services;

import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Person;
import com.project.hospital.repositorys.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private PersonRepository personRepository;

    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person getPerson(Long personId){
        System.out.println("Service calling getPerson");
        Optional<Person> person = this.personRepository.findById(personId);
        if(person.isPresent()){
            return person.get();
        }else{
            throw new InformationNotFoundException("No person with the id " + personId);
        }
    }

    public List<Person> getPersons(){
        System.out.println("Service calling getPersons");
        return this.personRepository.findAll();
    }

    
}
