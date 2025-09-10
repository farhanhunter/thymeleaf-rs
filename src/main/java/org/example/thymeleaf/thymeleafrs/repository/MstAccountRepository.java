package org.example.thymeleaf.thymeleafrs.repository;

import org.example.thymeleaf.thymeleafrs.entity.MstAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MstAccountRepository extends JpaRepository<MstAccount, Long> {
    Optional<MstAccount> findByUsername(String username);
    Optional<MstAccount> findByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("update MstAccount m set m.tokenHash = null where m.username = :username")
    int clearTokenHashByUsername(@Param("username") String username);
}
