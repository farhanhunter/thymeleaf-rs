package org.example.thymeleaf.thymeleafrs.controller;

import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> getPhoneInfo(@RequestParam String phone) {
        PhoneInfoResponse data = phoneInfoService.getByPhone(phone);
        return ResponseEntity.ok(new BaseResponse<>("200", false, "Success", data));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> createPhoneInfo(
            @RequestBody PhoneInfoRequest request,
            @AuthenticationPrincipal String username) {
        PhoneInfoResponse data = phoneInfoService.savePhoneInfo(request, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>("201", false, "Created", data));
    }

    @PostMapping("/update")
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> updatePhoneInfo(
            @RequestParam String phone,
            @RequestBody PhoneInfoRequest request,
            @AuthenticationPrincipal String username) {
        PhoneInfoResponse data = phoneInfoService.updatePhoneInfo(phone, request, username);
        return ResponseEntity.ok(new BaseResponse<>("200", false, "Updated", data));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> deletePhoneInfo(
            @RequestParam String phone,
            @AuthenticationPrincipal String username) {
        PhoneInfoResponse data = phoneInfoService.deletePhoneInfo(phone, username);
        return ResponseEntity.ok(new BaseResponse<>("200", false, "Deleted", data));
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<PhoneInfoResponse>>> getContactList(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PhoneInfoResponse> data = phoneInfoService.getContactList(query, page, size);
        String message = data.isEmpty() ? "No data found" : "Success";
        return ResponseEntity.ok(new BaseResponse<>("200", false, message, data));
    }
}