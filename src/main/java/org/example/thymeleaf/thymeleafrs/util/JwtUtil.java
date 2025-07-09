package org.example.thymeleaf.thymeleafrs.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "0BE4i8tUgPICAZ2FiE3xvICwgdmHVDOx";
    private static final Long EXPIRATION_TIME = 864_000_000L;
    private static final Key SECRET_KEY_OBJ = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY_OBJ, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY_OBJ)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY_OBJ)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
