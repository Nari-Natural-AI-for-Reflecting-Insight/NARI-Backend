package com.naribackend.infra.mail;

import com.naribackend.core.email.EmailSender;
import com.naribackend.core.email.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(emailMessage.getToEmailAddress());
        msg.setSubject(emailMessage.subject());
        msg.setText(emailMessage.content());

        javaMailSender.send(msg);
    }
}
