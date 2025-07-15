package org.example.thymeleaf.thymeleafrs.controller;

import jakarta.validation.Valid;
import org.example.thymeleaf.thymeleafrs.dto.request.LoginRequest;
import org.example.thymeleaf.thymeleafrs.dto.request.RegisterRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.example.thymeleaf.thymeleafrs.dto.response.LoginResponse;
import org.example.thymeleaf.thymeleafrs.entity.MstAccount;
import org.example.thymeleaf.thymeleafrs.service.MstAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MstAccountService mstAccountService;

    public AuthController(MstAccountService mstAccountService) {
        this.mstAccountService = mstAccountService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<MstAccount>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        MstAccount user = mstAccountService.register(registerRequest);
        BaseResponse<MstAccount> response = new BaseResponse<>("200", false, "User registered successfully", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = mstAccountService.login(loginRequest);
        BaseResponse<LoginResponse> response = new BaseResponse<>("200", false, "Login successful", loginResponse);
        return ResponseEntity.ok(response);
    }
}
