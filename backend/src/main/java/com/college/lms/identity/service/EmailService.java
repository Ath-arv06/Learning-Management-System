package com.college.lms.identity.service;

/**
 * Email sending abstraction.
 *
 * <p>In development (app.email.enabled=false), all implementations log the email
 * to the console instead of sending it. This allows the verification and reset
 * workflows to be tested without an SMTP server.
 *
 * <p>In production (app.email.enabled=true), the SMTP-backed implementation sends
 * real emails via JavaMailSender.
 */
public interface EmailService {
    void sendEmailVerification(String toEmail, String firstName, String plainToken);
    void sendPasswordReset(String toEmail, String firstName, String plainToken);
    void sendGenericNotification(String toEmail, String subject, String bodyHtml);
}
