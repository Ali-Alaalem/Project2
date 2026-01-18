package com.project.hospital.services;

import com.cloudinary.Cloudinary;
import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Person;
import com.project.hospital.models.User;
import com.project.hospital.repositorys.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {
    private PersonRepository personRepository;
    private final Cloudinary cloudinary;

    @Autowired
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
    public Person createPerson(MultipartFile multipartFile, Person person) throws IOException {
        System.out.println("Service is calling createPerson");

           String imageURL= cloudinary.uploader().upload(multipartFile.getBytes(),
                    Map.of("public_id", UUID.randomUUID().toString())).get("url").toString();
           person.setPhoto(imageURL);
            return this.personRepository.save(person);

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

    public Person getPersonByUser(User user){
        System.out.println("Service is calling ==> getPersonByUser()");
        Optional<Person> person = this.personRepository.findByUser(user);
        if(person.isPresent()){
            return person.get();
        }else{
            throw new InformationNotFoundException("User " + user.getId() + " has no person details");
        }
    }
}
