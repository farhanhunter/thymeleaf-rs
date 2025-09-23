package org.example.thymeleaf.thymeleafrs.repository;

import org.example.thymeleaf.thymeleafrs.constant.SourceType;
import org.example.thymeleaf.thymeleafrs.entity.MstPhoneInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhoneInfoRepository extends JpaRepository<MstPhoneInfo, Long> {
    Optional<MstPhoneInfo> findByPhone(String phone);
    Optional<MstPhoneInfo> findByPhoneAndSource(String phone, SourceType source);
    List<MstPhoneInfo> findAllByPhone(String phone);
    List<MstPhoneInfo> findAllByPhoneAndSource(String phone, SourceType source);

    @Query(value = """
    SELECT * FROM mst_phone_info
    WHERE (:phone IS NULL OR phone = :phone)
      AND (:source IS NULL OR "source" = :source)
      AND (
        :query IS NULL OR :query = '' OR
        LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) OR
        phone LIKE CONCAT('%', :query, '%')
      )
    """,
            countQuery = """
    SELECT COUNT(*) FROM mst_phone_info
    WHERE (:phone IS NULL OR phone = :phone)
      AND (:source IS NULL OR "source" = :source)
      AND (
        :query IS NULL OR :query = '' OR
        LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) OR
        phone LIKE CONCAT('%', :query, '%')
      )
    """,
            nativeQuery = true)
    Page<MstPhoneInfo> searchContacts(
            @Param("query") String query,
            @Param("phone") String phone,
            @Param("source") String source,
            Pageable pageable);
}