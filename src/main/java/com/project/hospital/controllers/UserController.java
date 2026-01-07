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

//    @GetMapping("/login/{userId}/person")
//    public Person getPerson(@PathVariable("userId") Long userId){
//        System.out.println("Controller calling ==> getPerson()");
//        User user = this.userService.getUser(userId);
//        Person person = this.personService
//    }
}
