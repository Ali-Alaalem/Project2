package com.project.hospital.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {


    @GetMapping("/hello")
    public String HelloWorld(){
        return"Hello world";
    }
}
