package com.org.Activity_Tracker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class Config {

    private String jwtSecret;

    private String jwtExpirationMs;
}
