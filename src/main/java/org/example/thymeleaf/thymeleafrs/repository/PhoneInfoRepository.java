package org.example.thymeleaf.thymeleafrs.repository;

import org.example.thymeleaf.thymeleafrs.entity.MstPhoneInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PhoneInfoRepository extends JpaRepository<MstPhoneInfo, Long> {
    Optional<MstPhoneInfo> findByPhone(String phone);

    @Query(
            value = """
            SELECT * FROM mst_phone_info 
            WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR phone LIKE CONCAT('%', :query, '%')
            """,
            countQuery = """
            SELECT count(*) FROM mst_phone_info 
            WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR phone LIKE CONCAT('%', :query, '%')
            """,
            nativeQuery = true
    )
    Page<MstPhoneInfo> searchContacts(@Param("query") String query, Pageable pageable);

    Optional<MstPhoneInfo> findByPhoneAndSource(String phone, String source);
}