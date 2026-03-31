package com.example.aiagent.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @PostConstruct
    public void init() {
        if (!twilioAccountSid.isBlank() && !twilioAuthToken.isBlank()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
        }
    }

    // ── Envoyer SMS via Twilio ─────────────────────────────
    public void sendSms(String toPhone, String message) {
        if (twilioAccountSid.isBlank())
            throw new RuntimeException("Twilio non configuré");

        Message.creator(
                new PhoneNumber(toPhone),
                new PhoneNumber(twilioPhoneNumber),
                message
        ).create();
    }

    // ── Envoyer Email via JavaMail ─────────────────────────
    public void sendEmail(String toEmail, String subject, String body) {
        if (mailSender == null)
            throw new RuntimeException("JavaMail non configuré");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur envoi email : " + e.getMessage());
        }
    }
}