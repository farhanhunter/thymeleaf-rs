package org.example.thymeleaf.thymeleafrs.service;

import org.example.thymeleaf.thymeleafrs.constant.SourceType;
import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import java.util.List;

public interface PhoneInfoService {
    List<PhoneInfoResponse> getByPhone(String phone, @Nullable SourceType source);
    PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request, String username);
    PhoneInfoResponse updatePhoneInfo(Long id, PhoneInfoRequest request, String username);
    PhoneInfoResponse deletePhoneInfo(String phone, String username);
    Page<PhoneInfoResponse> getContactList(String query, String phone, SourceType source, int page, int size);
}
