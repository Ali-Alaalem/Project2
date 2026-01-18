package com.project.hospital.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hospital.models.PasswordChangeRequest;
import com.project.hospital.models.Person;
import com.project.hospital.models.User;
import com.project.hospital.models.request.CreateDoctorRequest;
import com.project.hospital.models.request.LoginRequest;
import com.project.hospital.services.PersonService;
import com.project.hospital.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {
    private UserService userService;
    private PersonService personService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPersonService(PersonService personService){
        this.personService = personService;
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User objectUser){
        System.out.println("Calling create user");
        return userService.createUser(objectUser);
    }

    @PostMapping("/doctors")
    @PreAuthorize("hasAuthority('user:create')")
    public User createDoctor(@RequestBody CreateDoctorRequest request) {
        return userService.createDoctor(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        System.out.println("Calling loginUser ==>");
        return userService.loginUser(loginRequest);

    }

    @PostMapping("/password/reset")
    public void resetPasswordEmailSender(@RequestBody User user){
        System.out.println("Calling resetPasswordEmailSender ==>");
        userService.resetPasswordEmailSender(user);
    }

    @GetMapping("/password/reset/page")
    public ResponseEntity<String> resetPasswordPage(@RequestParam("token") String token){
        System.out.println("Calling resetPasswordPage ==>");
       return userService.resetPasswordPage(token);
    }

    @PostMapping("/password/reset/submit")
    public ResponseEntity<String> resetPasswordSubmit(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("<h3>Password reset successfully!</h3>");
    }

    @PutMapping("/change/password")
    public User ChangePassword(Authentication authentication, @RequestBody PasswordChangeRequest request){
        System.out.println("Controller calling ==> ChangePassword()");
        return userService.ChangePassword(authentication,request);
    }


    @GetMapping("/{userId}/person")
    @PreAuthorize("hasAuthority('user:view')")
    public Person getPerson(@PathVariable("userId") Long userId){
        System.out.println("Controller calling ==> getPerson()");
        User user = this.userService.getUser(userId);
        return this.personService.getPersonByUser(user);
    }

    @PostMapping(
            value = "/{userId}/person",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAuthority('user:update')")
    public Person createPerson(
            @RequestPart("image") MultipartFile image,
            @RequestPart("person") String personJson,
            @PathVariable Long userId
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Person person = objectMapper.readValue(personJson, Person.class);

        User user = userService.getUser(userId);
        person.setUser(user);

        return personService.createPerson(image, person);
    }


    @PutMapping("/{userId}/person")
    @PreAuthorize("hasAuthority('user:update')")
    public Person updatePerson(@PathVariable("userId") Long userId, @RequestBody Person person){
        System.out.println("Controller calling ==> updatePerson()");
        User user = this.userService.getUser(userId);
        person.setUser(user);
        return this.personService.updatePerson(person.getPersonId(), person);
    }


    @DeleteMapping("/{userId}/person")
    @PreAuthorize("hasAuthority('user:delete')")
    public Person deletePerson(@PathVariable("userId") Long userId){
        return this.personService.deletePerson(
                    this.personService.getPersonByUser(
                            this.userService.getUser(userId)
                    ).getPersonId()
        );
    }





}
