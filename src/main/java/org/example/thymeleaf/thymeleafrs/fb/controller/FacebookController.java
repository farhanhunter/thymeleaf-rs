package org.example.thymeleaf.thymeleafrs.fb.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thymeleaf.thymeleafrs.fb.config.OAuthStateUtil;
import org.example.thymeleaf.thymeleafrs.fb.dto.FbMeDto;
import org.example.thymeleaf.thymeleafrs.fb.service.FacebookOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/fb")
@RequiredArgsConstructor
public class FacebookController {
    private final FacebookOAuthService facebookOAuthService;
    private final OAuthStateUtil oAuthStateUtil;
    public record RevokeReq(String fbUserId, String userAccessToken, Boolean hardDelete) {}
    public record RevokeResp(boolean revoked, String fbUserId) {}

    // ====== Pintu 1: Sessionful (langsung redirect) ======
    @GetMapping("/login")
    public void login(HttpServletResponse resp, HttpSession session) throws IOException {
        String s = oAuthStateUtil.randomNonce();
        session.setAttribute("OAUTH_STATE", s);
        resp.sendRedirect(facebookOAuthService.buildAuthUrl(s));
    }

    // ====== Pintu 2: Stateless (return JSON authorization_url + signed state) ======
    @GetMapping("/auth/url")
    public ResponseEntity<Map<String, String>> authUrl() {
        String signed = oAuthStateUtil.createSignedState(5 * 60_000L);
        String url = facebookOAuthService.buildAuthUrl(signed);
        return ResponseEntity.ok(Map.of(
                "state", signed,
                "authorization_url", url
        ));
    }

    // ====== Callback: validasi state via session ATAU signed ======
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam(required=false, name="error") String fbError,
                                      @RequestParam(required=false, name="error_description") String fbErrorDesc,
                                      @RequestParam String code,
                                      @RequestParam String state,
                                      HttpSession session) {
        if (fbError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Facebook error: " + fbError, "detail", fbErrorDesc));
        }

        // 1) coba validasi pakai session (pintu 1)
        String expected = (String) session.getAttribute("OAUTH_STATE");
        if (expected != null) {
            session.removeAttribute("OAUTH_STATE");
            if (!expected.equals(state)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid OAuth state (session mismatch)"));
            }
        } else {
            // 2) fallback: validasi signed state (pintu 2)
            if (!oAuthStateUtil.verifySignedState(state)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid OAuth state (signature/expired)"));
            }
        }

        var token = facebookOAuthService.exchangeCode(code);
        if (token == null || token.access_token() == null || token.access_token().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token exchange failed (access_token empty)"));
        }

        var me = facebookOAuthService.getMe(token.access_token());
        var saved = facebookOAuthService.saveOrUpdate(me, token);
        return ResponseEntity.ok(Map.of(
                "fbUserId", saved.getFbUserId(),
                "name", saved.getName(),
                "email", saved.getEmail(),
                "expiresAt", saved.getExpiresAt()
        ));
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
