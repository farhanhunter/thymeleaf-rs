package org.example.thymeleaf.thymeleafrs.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.thymeleaf.thymeleafrs.repository.MstAccountRepository;
import org.example.thymeleaf.thymeleafrs.util.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final MstAccountRepository mstAccountRepository;

    public JwtAuthFilter(MstAccountRepository mstAccountRepository) {
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
            if (StringUtils.hasText(token)) {
                if (JwtUtil.validateTokenWithDB(token, mstAccountRepository)) {
                    String username = JwtUtil.getUsernameFromToken(token);
                    String role = JwtUtil.getRoleFromToken(token);
                    if (username != null && role != null) {
                        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            logger.debug("JWT expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("Invalid JWT", e);
        }

        filterChain.doFilter(request, response);
    }
}