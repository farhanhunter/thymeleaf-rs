package org.example.thymeleaf.thymeleafrs.controller;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phoneinfo")
public class PhoneInfoController {

    private final PhoneInfoService phoneInfoService;

    @Autowired
    public PhoneInfoController(PhoneInfoService phoneInfoService) {
        this.phoneInfoService = phoneInfoService;
    }

    @GetMapping
    public BaseResponse<PhoneInfoResponse> getPhoneInfo(@RequestParam String phone) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "unknown";
        PhoneInfoResponse data = phoneInfoService.getByPhone(phone);
        data.setRequestedBy(username);
        return new BaseResponse<>("200", false, "Success", data);
    }

    @PostMapping
    public BaseResponse<PhoneInfoResponse> createPhoneInfo(@RequestBody PhoneInfoRequest request) {
        PhoneInfoResponse data = phoneInfoService.savePhoneInfo(request);
        return new BaseResponse<>("201", false, "Created", data);
    }

    @PutMapping
    public BaseResponse<PhoneInfoResponse> updatePhoneInfo(
            @RequestParam String phone,
            @RequestBody PhoneInfoRequest request) {
        PhoneInfoResponse data = phoneInfoService.updatePhoneInfo(phone, request);
        return new BaseResponse<>("200", false, "Updated", data);
    }

    @DeleteMapping
    public BaseResponse<PhoneInfoResponse> deletePhoneInfo(@RequestParam String phone) {
        PhoneInfoResponse data = phoneInfoService.deletePhoneInfo(phone);
        return new BaseResponse<>("200", false, "Deleted", data);
    }
}