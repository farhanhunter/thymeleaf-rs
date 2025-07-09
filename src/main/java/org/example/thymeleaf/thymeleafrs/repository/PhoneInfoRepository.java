package org.example.thymeleaf.thymeleafrs.repository;

import org.example.thymeleaf.thymeleafrs.entity.PhoneInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneInfoRepository extends JpaRepository<PhoneInfo, String> {
}
