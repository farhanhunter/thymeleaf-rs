package org.example.thymeleaf.thymeleafrs.repository;

import org.example.thymeleaf.thymeleafrs.entity.MstAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MstAccountRepository extends JpaRepository<MstAccount, Long> {
    Optional<MstAccount> findByUsername(String username);
    Optional<MstAccount> findByPhoneNumber(String phoneNumber);
}
