package org.example.thymeleaf.thymeleafrs.fb.dto;

public record FbDebugTokenResp(Data data) {
    public record Data(
            boolean is_valid,
            String app_id,
            String user_id,
            Long expires_at,
            String[] scopes
    ) {}
}
