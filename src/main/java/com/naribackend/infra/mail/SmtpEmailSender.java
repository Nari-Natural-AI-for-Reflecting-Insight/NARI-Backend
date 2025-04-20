package com.naribackend.infra.mail;

import com.naribackend.core.email.EmailSender;
import com.naribackend.core.email.EmailMessage;
import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Async("emailTaskExecutor")
    @Retryable(
            retryFor = { CoreException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(emailMessage.getToEmailAddress());
        msg.setSubject(emailMessage.subject());
        msg.setText(emailMessage.content());

        try {
            javaMailSender.send(msg);
        } catch (MailException e) {
            throw new CoreException(ErrorType.SEND_EMAIL_FAILED, e);
        }

    }

    /**
     * 이 메서드는 @Retryable이 실패한 후에 호출됩니다.
     */
    @Recover
    public void recover(CoreException ex, EmailMessage emailMessage) {
        log.error("All retry attempts failed for {}: {}", emailMessage.getToEmailAddress(), ex.toString());
    }
}
