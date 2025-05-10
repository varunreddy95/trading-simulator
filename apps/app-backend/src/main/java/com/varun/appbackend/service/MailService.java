package com.varun.appbackend.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.varun.appbackend.config.MailJetProperties;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending emails using Mailjet.
 */
@Service
public class MailService {

    private final MailJetProperties mailjetProperties;
    private MailjetClient client;

    public MailService(MailJetProperties mailjetProperties) {
        this.mailjetProperties = mailjetProperties;
    }

    @PostConstruct
    public void initClient() {
        this.client = new MailjetClient(ClientOptions.builder()
                .apiKey(mailjetProperties.getApiKey())
                .apiSecretKey(mailjetProperties.getSecretKey())
                .build());
    }

    /**
     * Sends a password reset email using Mailjet with a styled HTML body.
     *
     * @param toEmail The recipient's email address.
     * @param token   The generated password reset token.
     * @throws MailjetException if sending the email fails.
     */
    public void sendPasswordResetEmail(String toEmail, String token) throws MailjetException {
        String htmlContent = buildHtmlEmailContent(token);

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", mailjetProperties.getFromEmail())
                                        .put("Name", mailjetProperties.getFromName()))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject().put("Email", toEmail)))
                                .put(Emailv31.Message.SUBJECT, "Reset your password")
                                .put(Emailv31.Message.HTMLPART, htmlContent)));

        MailjetResponse response = client.post(request);

        if (response.getStatus() != 200) {
            throw new MailjetException("Failed to send reset email: " + response.getStatus());
        }
    }

    /**
     * Builds the styled HTML content for the password reset email.
     *
     * @param token The token to embed in the reset link.
     * @return HTML string to send via Mailjet.
     */
    private String buildHtmlEmailContent(String token) {
        String resetLink = "http://localhost:3000/reset-password?token=" + token; // adjust for production

        return """
            <div style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #333333;">Reset Your Password</h2>
                    <p>Hello,</p>
                    <p>You requested a password reset. Click the button below to set a new password:</p>
                    <p style="text-align: center;">
                        <a href="%s" style="display: inline-block; padding: 12px 24px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Reset Password</a>
                    </p>
                    <p>If you didn’t request this, you can safely ignore this email.</p>
                    <p style="color: #999999; font-size: 12px;">This link will expire in 15 minutes.</p>
                </div>
            </div>
        """.formatted(resetLink);
    }
}
