package org.example.thymeleaf.thymeleafrs.service;

import org.example.thymeleaf.thymeleafrs.dto.request.LoginRequest;
import org.example.thymeleaf.thymeleafrs.dto.request.RegisterRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.LoginResponse;
import org.example.thymeleaf.thymeleafrs.entity.MstAccount;

public interface MstAccountService {
    MstAccount register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    void unlockAccount(String username);
}
