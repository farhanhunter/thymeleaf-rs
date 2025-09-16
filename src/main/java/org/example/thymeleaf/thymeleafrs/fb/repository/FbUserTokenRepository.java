package org.example.thymeleaf.thymeleafrs.fb.repository;

import org.example.thymeleaf.thymeleafrs.fb.entity.FbUserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FbUserTokenRepository extends JpaRepository<FbUserToken, Long> {
    Optional<FbUserToken> findByFbUserId(String fbUserId);
}
