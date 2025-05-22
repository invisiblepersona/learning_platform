package com.learning_platform.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import com.learning_platform.auth.dto.ContactRequest;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping
    public String sendContactEmail(@RequestBody ContactRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("your_email@gmail.com"); // Your inbox
        mail.setSubject("📨 Contact Form Message from " + request.getName());
        mail.setText("From: " + request.getName() +
                     "\nEmail: " + request.getEmail() +
                     "\n\nMessage:\n" + request.getMessage());

        mailSender.send(mail);
        return "Message sent successfully!";
    }
}

