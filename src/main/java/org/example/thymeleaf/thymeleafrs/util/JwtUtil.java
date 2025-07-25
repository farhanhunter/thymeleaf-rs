package org.example.thymeleaf.thymeleafrs.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.thymeleaf.thymeleafrs.entity.MstAccount;
import org.example.thymeleaf.thymeleafrs.repository.MstAccountRepository;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

public class JwtUtil {
    private static final String SECRET_KEY = "0BE4i8tUgPICAZ2FiE3xvICwgdmHVDOx";
    private static final Long EXPIRATION_TIME = 864_000_000L;
    private static final Key SECRET_KEY_OBJ = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private JwtUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY_OBJ, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY_OBJ)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException _) {
            return null;
        }
    }

    public static String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY_OBJ)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException _) {
            return null;
        }
    }

    public static Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY_OBJ)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    public static boolean validateTokenWithDB(String token, MstAccountRepository mstAccountRepository) {
        if (!Boolean.TRUE.equals(validateToken(token))) return false;

        String username = getUsernameFromToken(token);
        if (username == null) return false;

        Optional<MstAccount> userOpt = mstAccountRepository.findByUsername(username);
        return userOpt.isPresent() && token.equals(userOpt.get().getToken());
    }
}
