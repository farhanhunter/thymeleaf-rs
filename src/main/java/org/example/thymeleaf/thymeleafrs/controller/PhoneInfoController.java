package org.example.thymeleaf.thymeleafrs.controller;

import jakarta.validation.Valid;
import org.example.thymeleaf.thymeleafrs.constant.SourceType;
import org.example.thymeleaf.thymeleafrs.dto.request.PhoneInfoRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.example.thymeleaf.thymeleafrs.dto.response.PhoneInfoResponse;
import org.example.thymeleaf.thymeleafrs.service.PhoneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phone-info")
public class PhoneInfoController {

    private final PhoneInfoService phoneInfoService;

    @Autowired
    public PhoneInfoController(PhoneInfoService phoneInfoService) {
        this.phoneInfoService = phoneInfoService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<PhoneInfoResponse>>> getPhoneInfo(
            @RequestParam String phone,
            @RequestParam(required = false) SourceType source) {

        var items = phoneInfoService.getByPhone(phone, source);
        return ResponseEntity.ok(new BaseResponse<>("200", false, "Success", items));
    }


    @PostMapping
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> create(
            @RequestBody @Valid PhoneInfoRequest req,
            @AuthenticationPrincipal String username) {
        var data = phoneInfoService.savePhoneInfo(req, username);
        return ResponseEntity.status(201)
                .body(new BaseResponse<>("201", false, "Created", data));
    }


    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PhoneInfoResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid PhoneInfoRequest req,
            @AuthenticationPrincipal String username) {
        var data = phoneInfoService.updatePhoneInfo(id, req, username);
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
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) SourceType source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var data = phoneInfoService.getContactList(query, phone, source, page, size);
        var message = data.isEmpty() ? "No data found" : "Success";
        return ResponseEntity.ok(new BaseResponse<>("200", false, message, data));
    }

}