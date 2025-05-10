package com.varun.appbackend.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for sending emails via MailJet
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "mailjet")
public class MailJetProperties {

    private String apiKey;

    private String secretKey;

    private String fromEmail;

    private String fromName;

}
