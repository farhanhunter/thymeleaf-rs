package org.example.thymeleaf.thymeleafrs.fb.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.thymeleaf.thymeleafrs.fb.config.FacebookProperties;
import org.example.thymeleaf.thymeleafrs.fb.dto.FbDebugTokenResp;
import org.example.thymeleaf.thymeleafrs.fb.dto.FbMeDto;
import org.example.thymeleaf.thymeleafrs.fb.entity.FbUserToken;
import org.example.thymeleaf.thymeleafrs.fb.repository.FbUserTokenRepository;
import org.example.thymeleaf.thymeleafrs.fb.service.FacebookOAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacebookOAuthServiceImpl implements FacebookOAuthService {

    private final FacebookProperties facebookProperties;
    private final WebClient graphClient;
    private final FbUserTokenRepository fbUserTokenRepository;

    @Override
    public String buildAuthUrl(String state) {
        String scope = urlEncode("public_profile,email");
        String redirect = urlEncode(facebookProperties.getRedirectUri());
        return "https://www.facebook.com/v19.0/dialog/oauth?client_id=" + facebookProperties.getAppId()
                + "&redirect_uri=" + redirect
                + "&response_type=code&state=" + urlEncode(state)
                + "&scope=" + scope;
    }

    @Override
    public TokenResp exchangeCode(String code) {
        return graphClient.get()
                .uri(uri -> uri.path("/oauth/access_token")
                        .queryParam("client_id", facebookProperties.getAppId())
                        .queryParam("client_secret", facebookProperties.getAppSecret())
                        .queryParam("redirect_uri", facebookProperties.getRedirectUri())
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(), resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new IllegalStateException("Token exchange failed: " + body))
                )
                .bodyToMono(TokenResp.class)
                .block();
    }


    @Override
    public FbMeDto getMe(String accessToken) {
        var uri = org.springframework.web.util.UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v19.0/me")
                .queryParam("fields", "id,name,email")      // tanpa picture{url}
                .queryParam("access_token", accessToken)
                .build(true).toUri();                       // encode sekali

        return graphClient.get().uri(uri).retrieve()
                .bodyToMono(FbMeDto.class).block();
    }

    @Override
    @Transactional
    public FbUserToken saveOrUpdate(FbMeDto me, TokenResp token) {
        FbUserToken entity = fbUserTokenRepository.findByFbUserId(me.getId()).orElseGet(FbUserToken::new);
        entity.setFbUserId(me.getId());
        entity.setName(me.getName());
        entity.setEmail(me.getEmail());
        entity.setAccessToken(token.access_token());
        if (token.expires_in() != null) {
            entity.setExpiresAt(Instant.now().plus(token.expires_in(), ChronoUnit.SECONDS));
        }
        if (entity.getCreatedAt() == null) entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return fbUserTokenRepository.save(entity);
    }

    @Override
    public Optional<FbUserToken> findByUserId(String fbUserId) {
        return fbUserTokenRepository.findByFbUserId(fbUserId);
    }

    @Override
    @Transactional
    public void deleteByUserId(String fbUserId) {
        fbUserTokenRepository.findByFbUserId(fbUserId).ifPresent(fbUserTokenRepository::delete);
    }

    @Override
    public boolean revokeByUserAccessToken(String userAccessToken) {
        record RevokeResp(Boolean success) {}

        String input = userAccessToken == null ? "" : userAccessToken.trim();
        if (input.isEmpty()) return false;

        RevokeResp resp = graphClient.delete()
                .uri(b -> b.path("/me/permissions")
                        .queryParam("access_token", input)
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(), r ->
                        r.bodyToMono(String.class).map(body ->
                                new IllegalStateException("Revoke failed: " + body)))
                .bodyToMono(RevokeResp.class)
                .block();

        return resp != null && Boolean.TRUE.equals(resp.success());
    }

    @Override
    @Transactional
    public boolean revokeByFbUserId(String fbUserId) {
        var opt = fbUserTokenRepository.findByFbUserId(fbUserId);
        if (opt.isEmpty()) return false;

        var token = opt.get().getAccessToken();
        boolean ok;
        try {
            ok = revokeByUserAccessToken(token);
        } finally {
            fbUserTokenRepository.delete(opt.get());
        }
        return ok;
    }

    @Override
    public Optional<String> resolveUserIdFromToken(String userAccessToken) {
        String input = userAccessToken == null ? "" : userAccessToken.trim();
        if (input.isEmpty()) return Optional.empty();

        String appAccess = facebookProperties.getAppId() + "|" + facebookProperties.getAppSecret();

        FbDebugTokenResp resp = graphClient.get()
                .uri(b -> b.path("/debug_token")
                        .queryParam("input_token", input)
                        .queryParam("access_token", appAccess)
                        .build())
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(), r ->
                        r.bodyToMono(String.class).map(body ->
                                new IllegalStateException("debug_token failed: " + body)))
                .bodyToMono(FbDebugTokenResp.class)
                .block();

        if (resp == null || resp.data() == null) return Optional.empty();

        boolean ok = resp.data().is_valid()
                && facebookProperties.getAppId().equals(resp.data().app_id());

        return ok ? Optional.ofNullable(resp.data().user_id()) : Optional.empty();
    }

    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
