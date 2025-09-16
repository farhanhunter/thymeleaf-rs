package org.example.thymeleaf.thymeleafrs.fb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "fb_user_token", indexes = {
        @Index(name = "idx_fb_user_id", columnList = "fb_user_id", unique = true)
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FbUserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fb_user_id", nullable = false, unique = true)
    private String fbUserId;

    private String name;
    private String email;

    @Column(name = "acces_token", nullable = false, length = 2048)
    private String accessToken;

    private Instant expiresAt;

    private Instant createdAt;
    private Instant updatedAt;
}
