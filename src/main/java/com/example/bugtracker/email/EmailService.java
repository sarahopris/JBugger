package com.example.bugtracker.email;

import com.example.bugtracker.Repository.IUserRepository;
import com.example.bugtracker.model.Bug;
import com.example.bugtracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * in this class we have all the functions to send emails with specific messages
 */
@Component
public class EmailService {
    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    private JavaMailSender emailSender;

    /**
     * When a new user is added it sends an email to the specified email address with the generated username.
     */
    public ResponseEntity<?> sendEmail(String firstName, String lastName, String username, String emailUser) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tothnaomi0618@gmail.com");
        message.setTo(emailUser);
        message.setSubject("Welcome to JBugger!");
        message.setText("Hello" + " " + firstName + " " + lastName + "!\n\nYour credentials for this website are: \nUsername: " + username+"\nPassword: "+username+
                "\n\nPlease log into our application and change your password.\n\nKind regards,\n -The JBugger Team");
        emailSender.send(message);
        return new ResponseEntity<>("Sent mail to " + emailUser, HttpStatus.OK);
    }

    /**
     * When a new bug is added it sends an email to the user which is assigned to this new bug with the new informations.
     */
    public ResponseEntity<?> sendEmailWithBugSpecifications(User assignedTo, Bug bug) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tothnaomi0618@gmail.com");
        message.setTo(assignedTo.getEmail());
        message.setSubject("test");
        message.setText("Hello" + " " + assignedTo.getFirstName() + " " + assignedTo.getLastName() + "\n" + "One Bug which is assigned to you has changed. Here are the new details: \n" + bug.toString());
        emailSender.send(message);
        return new ResponseEntity<>("Sent mail to " + assignedTo.getEmail(), HttpStatus.OK);
    }
}