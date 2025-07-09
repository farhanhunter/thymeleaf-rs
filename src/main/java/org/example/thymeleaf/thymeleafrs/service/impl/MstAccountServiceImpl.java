package org.example.thymeleaf.thymeleafrs.service.impl;

import org.example.thymeleaf.thymeleafrs.dto.request.LoginRequest;
import org.example.thymeleaf.thymeleafrs.dto.request.RegisterRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.LoginResponse;
import org.example.thymeleaf.thymeleafrs.entity.MstAccount;
import org.example.thymeleaf.thymeleafrs.repository.MstAccountRepository;
import org.example.thymeleaf.thymeleafrs.service.MstAccountService;
import org.example.thymeleaf.thymeleafrs.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MstAccountServiceImpl implements MstAccountService {
    private final MstAccountRepository mstAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MstAccountServiceImpl(MstAccountRepository mstAccountRepository, PasswordEncoder passwordEncoder) {
        this.mstAccountRepository = mstAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public MstAccount register(RegisterRequest registerRequest) {
        MstAccount user = new MstAccount();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPasswordHash()));
        user.setName(registerRequest.getName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setEmail(registerRequest.getEmail());
        return mstAccountRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<MstAccount> userOpt = mstAccountRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPasswordHash(), userOpt.get().getPasswordHash())) {
            String token = JwtUtil.generateToken(userOpt.get().getUsername());
            return new LoginResponse(token);
        }
        throw new IllegalArgumentException("Login failed for user: " + loginRequest.getUsername());
    }

    @Override
    public MstAccount findByUsername(String username) {
        return mstAccountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
