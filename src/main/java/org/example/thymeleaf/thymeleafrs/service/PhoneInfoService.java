package org.example.thymeleaf.thymeleafrs.service;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.springframework.data.domain.Page;

public interface PhoneInfoService {
    PhoneInfoResponse getByPhone(String phone);
    PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request, String username);
    PhoneInfoResponse updatePhoneInfo(String phone, PhoneInfoRequest request, String username);
    PhoneInfoResponse deletePhoneInfo(String phone, String username);
    Page<PhoneInfoResponse> getContactList(String query, int page, int size);
}
