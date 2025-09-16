package org.example.thymeleaf.thymeleafrs.fb.service;

import org.example.thymeleaf.thymeleafrs.fb.dto.FbMeDto;
import org.example.thymeleaf.thymeleafrs.fb.entity.FbUserToken;

import java.util.Optional;

public interface FacebookOAuthService {
    /** Buat URL login OAuth Facebook (dialog) */
    String buildAuthUrl(String state);

    /** Tukar authorization code → access token */
    TokenResp exchangeCode(String code);

    /** Panggil /me di Graph API menggunakan access token */
    FbMeDto getMe(String accessToken);

    /** Simpan/update token & cache profil user */
    FbUserToken saveOrUpdate(FbMeDto me, TokenResp token);

    /** Ambil token user dari DB */
    Optional<FbUserToken> findByUserId(String fbUserId);

    /** Hapus token user dari DB */
    void deleteByUserId(String fbUserId);

    /** Cabut semua izin app untuk user (invalidate all user tokens for this app) */
    boolean revokeByUserAccessToken(String userAccessToken);

    /** Convenience: temukan token user by fbUserId, revoke, lalu hapus dari DB */
    boolean revokeByFbUserId(String fbUserId);

    /** Map userAccessToken → fb user id (via /debug_token) */
    Optional<String> resolveUserIdFromToken(String userAccessToken);

    // token exchange DTO
    record TokenResp(String access_token, String token_type, Long expires_in) {}
}
