package com.project.hospital.controllers;

import com.project.hospital.models.Person;
import com.project.hospital.models.User;
import com.project.hospital.models.request.LoginRequest;
import com.project.hospital.services.PersonService;
import com.project.hospital.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth/users")
public class UserController {
    private UserService userService;
    private PersonService personService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User objectUser){
        System.out.println("Calling create user");
        return userService.createUser(objectUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        System.out.println("Calling loginUser ==>");
        return userService.loginUser(loginRequest);

    }

    @GetMapping("/{userId}/person")
    public Person getPerson(@PathVariable("userId") Long userId){
        System.out.println("Controller calling ==> getPerson()");
        User user = this.userService.getUser(userId);
        return this.personService.getPersonByUser(user);
    }

    @PostMapping("/{userId}/person")
    public Person createPerson(@PathVariable("userId") Long userId, @RequestBody Person person){
        System.out.println("Controller calling ==> createPerson()");
        User user = this.userService.getUser(userId);
        person.setUser(user);
        return this.personService.createPerson(person);
    }

    @PutMapping("/{userId}/person")
    public Person updatePerson(@PathVariable("userId") Long userId, @RequestBody Person person){
        System.out.println("Controller calling ==> updatePerson()");
        User user = this.userService.getUser(userId);
        person.setUser(user);
        return this.personService.updatePerson(person.getPersonId(), person);
    }

    @DeleteMapping
    public Person deletePerson(@PathVariable("userId") Long userId){
        return this.personService.deletePerson(
                    this.personService.getPersonByUser(
                            this.userService.getUser(userId)
                    ).getPersonId()
        );
    }



}
