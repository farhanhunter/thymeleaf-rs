package org.example.thymeleaf.thymeleafrs.controller;

import org.example.thymeleaf.thymeleafrs.constant.Constant;
import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
        PhoneInfoResponse data = phoneInfoService.getByPhone(phone);
        return new BaseResponse<>("200", false, "Success", data);
    }

    @PostMapping
    public BaseResponse<PhoneInfoResponse> createPhoneInfo(
            @RequestBody PhoneInfoRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : Constant.UNKNOWN;
        PhoneInfoResponse data = phoneInfoService.savePhoneInfo(request, username);
        return new BaseResponse<>("201", false, "Created", data);
    }

    @PostMapping("/update")
    public BaseResponse<PhoneInfoResponse> updatePhoneInfo(
            @RequestParam String phone,
            @RequestBody PhoneInfoRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : Constant.UNKNOWN;
        PhoneInfoResponse data = phoneInfoService.updatePhoneInfo(phone, request, username);
        return new BaseResponse<>("200", false, "Updated", data);
    }

    @DeleteMapping
    public BaseResponse<PhoneInfoResponse> deletePhoneInfo(@RequestParam String phone) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : Constant.UNKNOWN;
        PhoneInfoResponse data = phoneInfoService.deletePhoneInfo(phone, username);
        return new BaseResponse<>("200", false, "Deleted", data);
    }

    @GetMapping("/list")
    public BaseResponse<Page<PhoneInfoResponse>> getContactList(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PhoneInfoResponse> data = phoneInfoService.getContactList(query, page, size);
        String message = data.isEmpty() ? "No data found" : "Success";
        return new BaseResponse<>("200", false, message, data);
    }
}