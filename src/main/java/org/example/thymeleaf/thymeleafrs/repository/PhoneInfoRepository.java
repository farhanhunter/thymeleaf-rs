package org.example.thymeleaf.thymeleafrs.repository;

import org.example.thymeleaf.thymeleafrs.entity.MstPhoneInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneInfoRepository extends JpaRepository<MstPhoneInfo, Long> {
    Optional<MstPhoneInfo> findByPhone(String phone);
}
