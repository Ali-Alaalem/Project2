package com.project.hospital.controllers;

import com.project.hospital.models.Person;
import com.project.hospital.models.Token;
import com.project.hospital.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class VerifyTokenController {
    private TokenService tokenService;

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestParam("token") String token){
      return ResponseEntity.ok(tokenService.verifyToken(token)) ;
    }


}
