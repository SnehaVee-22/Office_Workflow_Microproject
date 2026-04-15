package com.workflow.approval.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.sender.name}")
    private String senderName;

    @Value("${app.mail.sender.email}")
    private String senderEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendCredentials(String toEmail, String name, String password) {
        if (!mailEnabled) {
            log.info("[Email Disabled] Credentials email skipped for {}", toEmail);
            return;
        }
        String subject = "Your WorkFlow Approval System Login Credentials";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto">
              <div style="background:linear-gradient(135deg,#27235c,#97247e);padding:28px 32px;border-radius:10px 10px 0 0">
                <h1 style="color:white;margin:0;font-size:22px">WorkFlow Approval System</h1>
                <p style="color:rgba(255,255,255,0.75);margin:6px 0 0">Your login credentials</p>
              </div>
              <div style="background:#ffffff;padding:28px 32px;border:1px solid #e8e8ee;border-top:none;border-radius:0 0 10px 10px">
                <p style="color:#222233;font-size:15px">Hello <strong>%s</strong>,</p>
                <p style="color:#555566;font-size:14px">Your account has been created. Use the credentials below to log in.</p>
                <div style="background:#f5f5f7;border-radius:8px;padding:16px 20px;margin:20px 0">
                  <p style="margin:0 0 8px"><span style="color:#9999aa;font-size:12px;text-transform:uppercase;letter-spacing:0.5px">Email</span><br>
                  <strong style="color:#27235c;font-size:15px">%s</strong></p>
                  <p style="margin:0"><span style="color:#9999aa;font-size:12px;text-transform:uppercase;letter-spacing:0.5px">Password</span><br>
                  <strong style="color:#27235c;font-size:15px">%s</strong></p>
                </div>
                <p style="color:#e01950;font-size:13px">⚠️ Please change your password after your first login.</p>
                <p style="color:#9999aa;font-size:12px;margin-top:24px">If you did not request this, contact your administrator.</p>
              </div>
            </div>
            """.formatted(name, toEmail, password);

        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    public void sendStatusNotification(String toEmail, String name, String requestId, String status, String remarks) {
        if (!mailEnabled) {
            log.info("[Email Disabled] Status notification skipped for {} – {}", toEmail, requestId);
            return;
        }
        String statusColor = status.equals("APPROVED") ? "#1a8c4e" : "#e01950";
        String statusIcon  = status.equals("APPROVED") ? "✅" : "❌";
        String subject = statusIcon + " Your Request " + requestId + " has been " + status;
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto">
              <div style="background:linear-gradient(135deg,#27235c,#97247e);padding:24px 32px;border-radius:10px 10px 0 0">
                <h1 style="color:white;margin:0;font-size:20px">Request %s</h1>
              </div>
              <div style="background:#ffffff;padding:24px 32px;border:1px solid #e8e8ee;border-radius:0 0 10px 10px">
                <p>Hello <strong>%s</strong>,</p>
                <p>Your request <strong>%s</strong> has been <span style="color:%s;font-weight:700">%s</span>.</p>
                %s
                <p style="color:#9999aa;font-size:12px;margin-top:20px">Log in to view the full details.</p>
              </div>
            </div>
            """.formatted(
                status, name, requestId, statusColor, status,
                (remarks != null && !remarks.isBlank())
                    ? "<div style='background:#f5f5f7;border-radius:8px;padding:14px 18px;margin:16px 0'><p style='margin:0;color:#555566;font-size:13px'><strong>Manager Remarks:</strong> " + remarks + "</p></div>"
                    : ""
            );

        sendHtmlEmail(toEmail, subject, html);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail, senderName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} – Subject: {}", to, subject);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
