package org.example.thymeleaf.thymeleafrs.fb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class OAuthStateUtil {

    @Value("${oauth.state.secret:change-me-super-secret-key}")
    private String stateSecret;

    private static final SecureRandom SR = new SecureRandom();

    public String randomNonce() {
        byte[] buf = new byte[16];
        SR.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stateSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC error", e);
        }
    }

    public String createSignedState(long ttlMillis) {
        String nonce = randomNonce();
        long exp = System.currentTimeMillis() + ttlMillis;
        String payload = nonce + "." + exp;
        String sig = hmac(payload);
        String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return payloadB64 + "." + sig;
    }

    public boolean verifySignedState(String state) {
        if (state == null || state.isBlank()) return false;
        String[] parts = state.split("\\.");
        if (parts.length != 2) return false;

        String payloadB64 = parts[0];
        String sig = parts[1];

        String payload = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
        String expectedSig = hmac(payload);
        if (!expectedSig.equals(sig)) return false;

        String[] p = payload.split("\\.");
        if (p.length != 2) return false;

        long exp = Long.parseLong(p[1]);
        return System.currentTimeMillis() <= exp;
    }
}

