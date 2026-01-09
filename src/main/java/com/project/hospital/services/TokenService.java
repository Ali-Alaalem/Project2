package com.project.hospital.services;



import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

        try{
            //For my Collaborators I'm  using this (MimeMessage) to enable the Html in the email the user will receive to verify his email when he registered.
            //but in normal scenarios (JavaMailSender) this is the library responsible for sending the email.
            MimeMessage message =mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true,"UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject("Verify your email address");

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
              <h1 style="color:#4F46E5; font-family:'Helvetica',Arial,sans-serif;">Welcome to Your App!</h1>
              <p style="font-size:16px; color:#333;">Hello,</p>
              <p style="font-size:16px; color:#333;">Thank you for registering. Please verify your email address to activate your account.</p>
              <a href="{link}" style="display:inline-block; padding:14px 25px; font-size:16px; font-weight:bold; color:#ffffff; background-color:#4F46E5; text-decoration:none; border-radius:8px;">Verify Email</a>
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
}
