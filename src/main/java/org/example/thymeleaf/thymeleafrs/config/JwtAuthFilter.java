package org.example.thymeleaf.thymeleafrs.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.thymeleaf.thymeleafrs.repository.MstAccountRepository;
import org.example.thymeleaf.thymeleafrs.util.JwtUtil;
import org.example.thymeleaf.thymeleafrs.util.TokenHasher;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MstAccountRepository mstAccountRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, MstAccountRepository mstAccountRepository) {
        this.jwtUtil = jwtUtil;
        this.mstAccountRepository = mstAccountRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader)) {
            String h = authHeader.trim();
            if (h.regionMatches(true, 0, "Bearer", 0, "Bearer".length())) {
                token = h.substring("Bearer".length()).trim();
                if (token.startsWith("\"") && token.endsWith("\"") && token.length() > 1) {
                    token = token.substring(1, token.length() - 1);
                }
            }
        }

        try {
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                final String username = jwtUtil.getUsernameFromToken(token);
                final String role     = jwtUtil.getRoleFromToken(token);

                boolean ok = false;
                if (username != null && role != null) {
                    final String incomingHash = TokenHasher.sha256(token);
                    ok = mstAccountRepository.findByUsername(username)
                            .map(acc -> constantTimeEquals(
                                    incomingHash,
                                    Objects.toString(acc.getTokenHash(), "")
                            ))
                            .orElse(false);
                }

                if (ok) {
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    log.debug("Token hash mismatch or user not found for {}", username);
                }
            }
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT", e);
        }

        filterChain.doFilter(request, response);
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                (a == null ? new byte[0] : a.getBytes(StandardCharsets.UTF_8)),
                (b == null ? new byte[0] : b.getBytes(StandardCharsets.UTF_8))
        );
    }

}