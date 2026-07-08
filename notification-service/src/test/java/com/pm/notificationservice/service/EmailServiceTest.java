package com.pm.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    private EmailService emailService;

    @Test
    void sendMail() {
        emailService = new EmailService(mailSender, "test@mail.com");

        String to = "user@email.com";
        String subject = "Hello";
        String body = "Hello, World!";

        emailService.sendMail(to, subject, body);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assert message.getTo() != null;
        assertEquals("user@email.com", message.getTo()[0]);
        assertEquals("Hello", message.getSubject());
        assertEquals("Hello, World!", message.getText());
        assertEquals("test@mail.com", message.getFrom());
    }


}
