package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.*;
import com.project.hospital.models.request.CreateDoctorRequest;
import com.project.hospital.models.request.LoginRequest;
import com.project.hospital.models.response.LoginResponse;
import com.project.hospital.repositorys.*;
import com.project.hospital.security.JWTUtils;
import com.project.hospital.security.MyUserDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final JavaMailSender mailSender;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final RoomRepository roomRepository;
    private final AppointmentRepository appointmentRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    @Value("${sender.email}")
    private String senderEmail;

    public UserService( VerificationTokenRepository verificationTokenRepository,TokenService tokenService, TokenRepository tokenRepository, UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils, @Lazy AuthenticationManager authenticationManager, @Lazy MyUserDetails myUserDetails, RoleRepository roleRepository, JavaMailSender mailSender,
                       TreatmentTypeRepository treatmentTypeRepository, RoomRepository roomRepository, AppointmentRepository appointmentRepository){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtils=jwtUtils;
        this.authenticationManager=authenticationManager;
        this.myUserDetails=myUserDetails;
        this.roleRepository=roleRepository;
        this.tokenRepository=tokenRepository;
        this.tokenService=tokenService;
        this.mailSender = mailSender;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.roomRepository = roomRepository;
        this.appointmentRepository = appointmentRepository;
        this.verificationTokenRepository=verificationTokenRepository;
    }

    public User findUserByEmailAddress(String email) {
        return userRepository.findUserByEmailAddress(email);
    }

    @Transactional
    public User createUser(User objectUser){
        if(!userRepository.existsByEmailAddress(objectUser.getEmailAddress())){
User theUser=userRepository.findUserByEmailAddress(objectUser.getEmailAddress());

            objectUser.setPassword(passwordEncoder.encode(objectUser.getPassword()));
            Optional<Role> role=roleRepository.findByName("PATIENT");
            objectUser.setIsVerified(false);
            objectUser.setRole(role.get());
            objectUser.setIsDeleted(false);
            User user=userRepository.save(objectUser);

            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setUser(user);
            verificationToken.setToken(UUID.randomUUID().toString());
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

            verificationTokenRepository.save(verificationToken);
            tokenService.sendMail(user.getEmailAddress(), verificationToken.getToken());

            return user;
        }else{ throw new InformationExistException("User with email address " +objectUser.getEmailAddress() + "already exist"); }
    }

    public User createDoctor(CreateDoctorRequest request) {
        if (request.getEmailAddress() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        if (userRepository.existsByEmailAddress(request.getEmailAddress())) {
            throw new InformationExistException("User with email address " + request.getEmailAddress() + "already exist");
        }

        Role doctorRole = roleRepository.findByName("DOCTOR").orElseThrow(
                () -> new InformationNotFoundException("DOCTOR role not found")
        );

        TreatmentType treatmentType = treatmentTypeRepository.findById(request.getTreatmentTypeId()).orElseThrow(
                () -> new InformationNotFoundException("Treatment type does not exist")
        );

        Room room = roomRepository.findByRoomTreatmentType(treatmentType).orElseThrow(
                () -> new InformationNotFoundException("No room found for this treatment type")
        );

        User doctor = new User();
        doctor.setFullName(request.getFullName());
        doctor.setEmailAddress(request.getEmailAddress());
        doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        doctor.setIsVerified(true);
        doctor.setRole(doctorRole);
        doctor.setUserTreatmentType(treatmentType);
        doctor.setWorkDaysAndHours(request.getWorkDaysAndHours());

        User savedDoctor = userRepository.save(doctor);

        createDoctorAppointmentsForNextMonth(savedDoctor, room);

        return savedDoctor;
    }

    private void createDoctorAppointmentsForNextMonth(User doctor, Room room) {
        HashMap<String, HashMap<String, LocalTime>> schedule = doctor.getWorkDaysAndHours();
        if (schedule == null || schedule.isEmpty()) {
            return;
        }

        HashMap<String, HashMap<String, LocalTime>> normalized = new HashMap<>();
        for (Map.Entry<String, HashMap<String, LocalTime>> entry : schedule.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                normalized.put(entry.getKey().toLowerCase(), entry.getValue());
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        List<Appointment> toCreate = new ArrayList<>();

        for (LocalDate date = today; date.isBefore(endDate); date = date.plusDays(1)) {
            String dayKey = date.getDayOfWeek().name().toLowerCase();
            HashMap<String, LocalTime> hours = normalized.get(dayKey);
            if (hours == null) {
                continue;
            }

            LocalTime from = hours.get("from");
            LocalTime to = hours.get("to");
            if (from == null || to == null || !from.isBefore(to)) {
                continue;
            }

            LocalDateTime start = LocalDateTime.of(date, from);
            LocalDateTime end = LocalDateTime.of(date, to);

            while (start.plusMinutes(30).compareTo(end) <= 0) {
                LocalDateTime slotEnd = start.plusMinutes(30);

                boolean doctorConflict = appointmentRepository.existsByDoctorIdAndStartTimeBetween(
                        doctor.getId(),
                        start,
                        slotEnd.minusNanos(1)
                );
                boolean roomConflict = appointmentRepository.existsByRoomIdAndStartTimeBetween(
                        room.getId(),
                        start,
                        slotEnd.minusNanos(1)
                );

                if (!doctorConflict && !roomConflict) {
                    Appointment appointment = new Appointment();
                    appointment.setDoctor(doctor);
                    appointment.setRoom(room);
                    appointment.setStartTime(start);
                    appointment.setEndTime(slotEnd);
                    toCreate.add(appointment);
                }

                start = slotEnd;
            }
        }

        if (!toCreate.isEmpty()) {
            appointmentRepository.saveAll(toCreate);
        }
    }


    public ResponseEntity<?> loginUser(LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());
        try {
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails=(MyUserDetails) authentication.getPrincipal();
            if(myUserDetails.getUser().getIsVerified() &&myUserDetails.getUser().getIsDeleted()==false){
            final String JWT =jwtUtils.generateJwtToken(myUserDetails);
            return ResponseEntity.ok(new LoginResponse(JWT));
            }else{
                return ResponseEntity.ok(new LoginResponse("Your Account is not verified or deleted"));
            }
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
            User delUser = user.get();
            this.userRepository.delete(delUser);
            return delUser;
        }else{
            throw new InformationNotFoundException("No user with the id " + userId + "exists.");
        }
    }



    public void  softDeleteUser(Authentication authentication,Long userId){
        System.out.println("Service calling ==> deleteUser()");
        Optional<User> forginUser=userRepository.findById(userId);
        String currentLoggedUserEmail = authentication.getName();
        User userLoggedIn=userRepository.findUserByEmailAddress(currentLoggedUserEmail);
        if(userLoggedIn.getRole().getId()!=1 ){
            throw new InformationExistException("User does not have permission ");
        }
        else if(!forginUser.isPresent()) {
            throw new InformationExistException("the user wanted to be deleted is not exist");
        }
if(forginUser.get().getIsDeleted()==true){
    forginUser.get().setIsDeleted(false);
    userRepository.save(forginUser.get());
}else {
    forginUser.get().setIsDeleted(true);
    userRepository.save(forginUser.get());
}
    }


    @Transactional
    public void resetPasswordEmailSender(User user){
        User resetPassUser=userRepository.findUserByEmailAddress(user.getEmailAddress());
        if(resetPassUser != null) {

            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setUser(user);
            verificationToken.setToken(UUID.randomUUID().toString());
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

            verificationTokenRepository.save(verificationToken);
            tokenService.sendMail(user.getEmailAddress(), verificationToken.getToken());

        }
        else{
            throw new InformationExistException("User with email address " +resetPassUser.getEmailAddress() + "does not exist");
        }
    }





    public void sendMail(String email,String token){
     String link = "http://localhost:8080/auth/users/password/reset/page?token=" + token;

        try{
            //For my Collaborators I'm  using this (MimeMessage) to enable the Html in the email the user will receive to verify his email when he registered.
            //but in normal scenarios (JavaMailSender) this is the library responsible for sending the email.
            MimeMessage message =mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true,"UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");

            String html = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin:0; padding:0; background-color:#f4f4f7;">
  <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f7; height:100%; text-align:center;">
    <tr>
      <td align="center">
        <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; padding:30px; border-radius:12px; box-shadow:0 4px 15px rgba(0,0,0,0.1); text-align:center;">
          <tr>
            <td>
              <h1 style="color:#4F46E5; font-family:'Helvetica',Arial,sans-serif;">Welcome to Triple A Hospital</h1>
              <p style="font-size:16px; color:#333;">Hello,</p>
              <p style="font-size:16px; color:#333;">Click on the button to reset your password.</p>
              <a href="{link}" style="display:inline-block; padding:14px 25px; font-size:16px; font-weight:bold; color:#ffffff; background-color:#4F46E5; text-decoration:none; border-radius:8px;">Reset Password</a>
              <p style="margin-top:20px; font-size:12px; color:#999;">&copy; 2026 Hospital Management System</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
""";
            html = html.replace("{link}", link);
            helper.setText(html, true);
            mailSender.send(message);

        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public ResponseEntity<String> resetPasswordPage(String token){
        String html = """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <title>Reset Password</title>
    </head>
    <body style="text-align:center; margin-top:50px;">
      <h2>Reset Your Password</h2>
      <form action="/auth/users/password/reset/submit" method="post">
        <input type="hidden" name="token" value="%s" />
        <input type="password" name="newPassword" placeholder="New Password" required />
        <br><br>
        <button type="submit">Reset Password</button>
      </form>
    </body>
    </html>
    """.formatted(token);

        return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
    }

public void resetPassword(String token,String newPass){
    Optional<Token> userToken= tokenRepository.findByToken(token);
    if (userToken.isPresent() && userToken.get().getExpiryDate().isAfter(LocalDateTime.now()))
    {
        User user=userToken.get().getUser();
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);
        tokenRepository.delete(userToken.get());
    }
}

public User ChangePassword(Authentication authentication,PasswordChangeRequest request) {
    String currentLoggedUserEmail = authentication.getName();
    User userLoggedIn=userRepository.findUserByEmailAddress(currentLoggedUserEmail);

     if( passwordEncoder.matches(request.getCurrentPassword(),userLoggedIn.getPassword())){
         userLoggedIn.setPassword(passwordEncoder.encode(request.getNewPassword()));
         return userRepository.save(userLoggedIn);
    }else {
         throw new InformationExistException("The Current password is wrong");
     }

}

}
