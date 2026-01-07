package com.project.hospital.services;

import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Person;
import com.project.hospital.repositorys.PersonRepository;
import org.springframework.security.core.parameters.P;
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


    public Person updatePerson(Long personId, Person updatedPerson){
        System.out.println("Service calling updatePerson");
        Optional<Person> person = this.personRepository.findById(personId);
        if(person.isPresent()){
            updatedPerson.setPersonId(personId);
            return this.personRepository.save(updatedPerson);
        }else{
            throw new InformationNotFoundException("No person with the id " + personId);
        }
    }

    // to-do: consider making cpr the id field.
    public Person createPerson(Person person){
        System.out.println("Service is calling createPerson");
        if(this.personRepository.findById(person.getPersonId()).isEmpty()){
            return this.personRepository.save(person);
        }else{
            throw new InformationExistException("A person with the id " + person.getPersonId() + " already exists");
        }
    }

    public Person deletePerson(Long personId){
        System.out.println("Service is calling deletePerson");
        Optional<Person> person = this.personRepository.findById(personId);
        if(person.isPresent()){
            this.personRepository.deleteById(personId);
            return person.get();
        }else{
            throw new InformationNotFoundException("No person with the id " + personId);
        }
    }
}
