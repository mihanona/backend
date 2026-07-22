package com.mihanona.backend.shared.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Renders verify-email.html with real values, sends it via MailDev
     * in dev (captured at http://localhost:1080, never actually leaves
     * your machine) or a real SMTP provider in production.
     */
    public void sendVerificationEmail(String toEmail, String fullName, String rawToken) {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        // In dev, this just needs to exist — clicking it in MailDev's UI
        // won't hit a real frontend yet, since Angular doesn't exist.
        context.setVariable("verifyUrl", "http://localhost:4200/verify-email?token=" + rawToken);

        String htmlBody = templateEngine.process("verify-email", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Verify your Mihanona account");
            helper.setText(htmlBody, true); // true = this is HTML, not plain text
            mailSender.send(message);
        } catch (Exception e) {
            // Don't let a failed email crash registration — log it and move on.
            // (A proper logger comes later; for now this is enough to see it in console.)
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }
}