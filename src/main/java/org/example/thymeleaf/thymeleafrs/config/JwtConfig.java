package org.example.thymeleaf.thymeleafrs.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.thymeleaf.thymeleafrs.util.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    @Bean
    public SecretKey jwtSecretKey(JwtProperties props) {
        byte[] keyBytes;
        String s = props.getSecret();

        // Heuristik: coba decode Base64; kalau gagal, pakai UTF-8
        try {
            keyBytes = Decoders.BASE64.decode(s);
        } catch (IllegalArgumentException e) {
            keyBytes = s.getBytes(StandardCharsets.UTF_8);
        }

        // Pastikan panjang minimal 32 bytes untuk HS256
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret terlalu pendek (<32 bytes). Gunakan string lebih panjang atau Base64 256-bit+.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
