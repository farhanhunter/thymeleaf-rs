package org.example.thymeleaf.thymeleafrs.service;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;

public interface PhoneInfoService {
    PhoneInfoResponse getByPhone(String phone);
    PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request, String username);
    PhoneInfoResponse updatePhoneInfo(String phone, PhoneInfoRequest request, String username);
    PhoneInfoResponse deletePhoneInfo(String phone, String username);
}
