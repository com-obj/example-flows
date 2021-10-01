package com.obj.examplesflows.genericDataToMessage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "license-agreements")
public class LicenseAgreementProperties {
    private String adminEmail;
    private String emailTemplatePath;
}
