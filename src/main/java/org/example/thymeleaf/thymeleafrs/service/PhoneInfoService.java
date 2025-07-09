package org.example.thymeleaf.thymeleafrs.service;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;

public interface PhoneInfoService {
    PhoneInfoResponse getByPhone(String phone);
    PhoneInfoResponse savePhoneInfo(PhoneInfoRequest request);
    PhoneInfoResponse updatePhoneInfo(String phone, PhoneInfoRequest request);
    PhoneInfoResponse deletePhoneInfo(String phone);
}
