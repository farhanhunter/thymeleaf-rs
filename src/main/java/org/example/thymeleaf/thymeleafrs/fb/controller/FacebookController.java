package org.example.thymeleaf.thymeleafrs.fb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thymeleaf.thymeleafrs.fb.dto.FbMeDto;
import org.example.thymeleaf.thymeleafrs.fb.service.FacebookOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/fb")
@RequiredArgsConstructor
public class FacebookController {
    private final FacebookOAuthService facebookOAuthService;
    public record RevokeReq(String fbUserId, String userAccessToken, Boolean hardDelete) {}
    public record RevokeResp(boolean revoked, String fbUserId) {}

    @GetMapping("/auth/url")
    public ResponseEntity<Map<String, String>> authUrl(@RequestParam(defaultValue = "state123") String state) {
        return ResponseEntity.ok(Map.of("authorization_url", facebookOAuthService.buildAuthUrl(state)));
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code,
                                      @RequestParam(required = false) String state) {
        var token = facebookOAuthService.exchangeCode(code);
        if (token == null || token.access_token() == null || token.access_token().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Token exchange failed (access_token empty)"
            ));
        }
        try {
            var me = facebookOAuthService.getMe(token.access_token());
            var saved = facebookOAuthService.saveOrUpdate(me, token);
            return ResponseEntity.ok(Map.of(
                    "fbUserId", saved.getFbUserId(),
                    "name", saved.getName(),
                    "email", saved.getEmail(),
                    "expiresAt", saved.getExpiresAt()
            ));
        } catch (WebClientResponseException e) {
            log.error("Graph /me error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @GetMapping("/{fbUserId}/me")
    public ResponseEntity<FbMeDto> me(@PathVariable String fbUserId) {
        var token = facebookOAuthService.findByUserId(fbUserId).orElseThrow();
        return ResponseEntity.ok(facebookOAuthService.getMe(token.getAccessToken()));
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RevokeResp> revoke(@RequestBody RevokeReq req) {
        if ((req.userAccessToken() == null || req.userAccessToken().isBlank())
                && (req.fbUserId() == null || req.fbUserId().isBlank())) {
            return ResponseEntity.badRequest().body(new RevokeResp(false, null));
        }
        boolean revoked;
        String targetId = req.fbUserId();

        if (req.userAccessToken() != null && !req.userAccessToken().isBlank()) {
            // 1) map token → user_id
            var userIdOpt = facebookOAuthService.resolveUserIdFromToken(req.userAccessToken());
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new RevokeResp(false, null));
            }
            targetId = userIdOpt.get();

            // 2) revoke ke Facebook
            revoked = facebookOAuthService.revokeByUserAccessToken(req.userAccessToken());

            // 3) bersihkan DB (kalau ada)
            facebookOAuthService.deleteByUserId(targetId);
        } else if (targetId != null && !targetId.isBlank()) {
            // langsung pakai fbUserId yang ada di DB
            revoked = facebookOAuthService.revokeByFbUserId(targetId);
        } else {
            return ResponseEntity.badRequest().body(new RevokeResp(false, null));
        }

        return ResponseEntity.ok(new RevokeResp(revoked, targetId));
    }

    @DeleteMapping("/{fbUserId}")
    public ResponseEntity<Void> delete(@PathVariable String fbUserId) {
        facebookOAuthService.deleteByUserId(fbUserId);
        return ResponseEntity.noContent().build();
    }
}
