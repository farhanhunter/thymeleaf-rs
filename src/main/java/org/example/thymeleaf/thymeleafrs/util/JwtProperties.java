package org.example.thymeleaf.thymeleafrs.util;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /** Bisa raw string atau Base64 (akan kita deteksi) */
    private String secret;
    /** Dalam detik (contoh: 86400 = 24 jam) */
    private long expiration = 86400;
}
