package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Permission;
import com.project.hospital.models.Role;
import com.project.hospital.models.Token;
import com.project.hospital.models.User;
import com.project.hospital.models.request.LoginRequest;
import com.project.hospital.models.response.LoginResponse;
import com.project.hospital.repositorys.RoleRepository;
import com.project.hospital.repositorys.TokenRepository;
import com.project.hospital.repositorys.UserRepository;
import com.project.hospital.security.JWTUtils;
import com.project.hospital.security.MyUserDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;

@Service
public class UserService {
    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;
    private final Set<Permission> userPermissions=new HashSet<>();
    private RoleRepository roleRepository;
    private TokenRepository tokenRepository;
    private TokenService tokenService;

    public UserService( TokenService tokenService,TokenRepository tokenRepository,UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils, @Lazy AuthenticationManager authenticationManager, @Lazy MyUserDetails myUserDetails,RoleRepository roleRepository){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtils=jwtUtils;
        this.authenticationManager=authenticationManager;
        this.myUserDetails=myUserDetails;
        this.roleRepository=roleRepository;
        this.tokenRepository=tokenRepository;
        this.tokenService=tokenService;
    }

    public User findUserByEmailAddress(String email) {
        return userRepository.findUserByEmailAddress(email);
    }

    public User createUser(User objectUser){
        if(!userRepository.existsByEmailAddress(objectUser.getEmailAddress())){
            objectUser.setPassword(passwordEncoder.encode(objectUser.getPassword()));
            Optional<Role> role=roleRepository.findByName("Patient");
            objectUser.setRole(role.get());
            User user=userRepository.save(objectUser);
            String token= UUID.randomUUID().toString();
            Token verifyToken= new Token();
            verifyToken.setToken(token);
            verifyToken.setUser(user);
            tokenRepository.save(verifyToken);
            tokenService.sendMail(user.getEmailAddress(), token);

            return user;
        }else{ throw new InformationExistException("User with email address " +objectUser.getEmailAddress() + "already exist"); }
    }


    public ResponseEntity<?> loginUser(LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());
        try {
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails=(MyUserDetails) authentication.getPrincipal();
            final String JWT =jwtUtils.generateJwtToken(myUserDetails);
            return ResponseEntity.ok(new LoginResponse(JWT));
        }catch (Exception e){
            return ResponseEntity.ok(new LoginResponse("Error :User name of password is incorrect"));
        }

    }

    public User getUser(Long userId){
        System.out.println("Service calling ==> getUser()");
        Optional<User> user = this.userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new InformationNotFoundException("No user with the id " + userId + "exists.");
        }
    }

    public List<User> getUsers(){
        System.out.println("Service calling ==> getUsers()");
        return this.userRepository.findAll();
    }

    public User updateUser(Long userId, User user){
        System.out.println("Service calling ==> updateUser()");
        Optional<User> userObject = this.userRepository.findById(userId);
        if(userObject.isPresent()){
            user.setId(userId);
            return this.userRepository.save(user);
        }else{
            throw new InformationNotFoundException("No user with the id " + userId + "exists.");
        }
    }

    public User deleteUser(Long userId){
        System.out.println("Service calling ==> deleteUser()");
        Optional<User> user = this.userRepository.findById(userId);
        if(user.isPresent()){
            this.userRepository.delete(user.get());
            return user.get();
        }else{
            throw new InformationNotFoundException("No user with the id " + userId + "exists.");
        }
    }


}
