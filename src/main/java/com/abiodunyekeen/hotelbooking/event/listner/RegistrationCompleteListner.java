package com.abiodunyekeen.hotelbooking.event.listner;

import com.abiodunyekeen.hotelbooking.event.RegistrationCompleteEvent;
import com.abiodunyekeen.hotelbooking.model.User;
import com.abiodunyekeen.hotelbooking.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component

public class RegistrationCompleteListner  implements ApplicationListener<RegistrationCompleteEvent> {

    private User user;
    private final UserService userService;
    private final JavaMailSender mailSender;

    public RegistrationCompleteListner(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // Get the Newly Register User
        user = event.getUser();
        // Create Verification Token for the user
        String verificationToken = UUID.randomUUID().toString();
        // Save the verification token for the user
        userService.saveUserVerificationToken(user,verificationToken);
        //Build the verification url
        String url = event.getApplicationUrl() + "/verify-email?token="+verificationToken;
        // Send the email
        try{
            sendVerificationEmail(url);
        }catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {

        String subject ="Email Verification";
        String senderName= "User Registration";
        String content ="<p> Hi, " + user.getFirstName()+ ",</p>"+
                "<p> Thank you for registering with us," +
                " Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" + url + "\"> Verify your email to activate your account</a>"+
                "<p> Thank you <br> Abbeylink LTD";

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("admin@rashkemsoft.com.ng", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        mailSender.send(message);
    }
}
