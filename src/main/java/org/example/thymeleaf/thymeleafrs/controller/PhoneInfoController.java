package org.example.thymeleaf.thymeleafrs.controller;

import jakarta.validation.Valid;
import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phone-info")
public class PhoneInfoController {

    private final PhoneInfoService phoneInfoService;

    @Autowired
    public PhoneInfoController(PhoneInfoService phoneInfoService) {
        this.phoneInfoService = phoneInfoService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> getPhoneInfo(
            @RequestParam String phone) {
        var data = phoneInfoService.getByPhone(phone);
        return ResponseEntity.ok(new BaseResponse<>("200", false, "Success", data));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> create(
            @RequestBody @Valid PhoneInfoRequest req,
            @AuthenticationPrincipal String username) {
        var data = phoneInfoService.savePhoneInfo(req, username);
        return ResponseEntity.ok(new BaseResponse<>("201", false, "Created", data));
    }

    @PutMapping("/{phone}")
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> update(
            @PathVariable String phone,
            @RequestBody @Valid PhoneInfoRequest req,
            @AuthenticationPrincipal String username) {
        var data = phoneInfoService.updatePhoneInfo(phone, req, username);
        return ResponseEntity.ok(new BaseResponse<>("200", false, "Updated", data));
    }

    @DeleteMapping("/{phone}")
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> delete(
            @PathVariable String phone,
            @AuthenticationPrincipal String username) {
        var data = phoneInfoService.deletePhoneInfo(phone, username);
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