package com.college.lms.identity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Development email service — logs all emails to the console.
 * Active when app.email.enabled=false (the default in dev).
 *
 * <p>The verification and reset tokens are printed to the log so the developer
 * can test the full workflow without an SMTP server.
 *
 * <p>IMPORTANT: Never use this in production. The production SMTP implementation
 * is selected by setting app.email.enabled=true.
 */
@Service
@ConditionalOnProperty(name = "app.email.enabled", havingValue = "false", matchIfMissing = true)
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendEmailVerification(String toEmail, String firstName, String plainToken) {
        log.info("""
            ╔══════════════════════════════════════════════════╗
            ║  [DEV EMAIL] Email Verification                  ║
            ║  To: {}
            ║  Token: {}
            ║  Verification URL:
            ║  http://localhost:5173/verify-email?token={}
            ╚══════════════════════════════════════════════════╝
            """, toEmail, plainToken, plainToken);
    }

    @Override
    public void sendPasswordReset(String toEmail, String firstName, String plainToken) {
        log.info("""
            ╔══════════════════════════════════════════════════╗
            ║  [DEV EMAIL] Password Reset                      ║
            ║  To: {}
            ║  Token: {}
            ║  Reset URL:
            ║  http://localhost:5173/reset-password?token={}
            ╚══════════════════════════════════════════════════╝
            """, toEmail, plainToken, plainToken);
    }

    @Override
    public void sendGenericNotification(String toEmail, String subject, String bodyHtml) {
        log.info("[DEV EMAIL] To: {} | Subject: {} | Body: {}", toEmail, subject, bodyHtml);
    }
}
