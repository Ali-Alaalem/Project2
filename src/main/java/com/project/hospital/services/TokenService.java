package com.project.hospital.services;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final JavaMailSender mailSender;

    @Value("${sender.email}")
    private String senderEmail;
    public TokenService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String email, String token){
        String link = "http://localhost:8080/api/auth/verify?token=" + token;

        SimpleMailMessage mailMessage=new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(email);
        mailMessage.setSubject("Verify");
        mailMessage.setText(link);

        mailSender.send(mailMessage);

    }
}
