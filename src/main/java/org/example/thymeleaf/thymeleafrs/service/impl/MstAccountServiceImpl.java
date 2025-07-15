package org.example.thymeleaf.thymeleafrs.service.impl;

import org.example.thymeleaf.thymeleafrs.dto.request.LoginRequest;
import org.example.thymeleaf.thymeleafrs.dto.request.RegisterRequest;
import org.example.thymeleaf.thymeleafrs.dto.response.LoginResponse;
import org.example.thymeleaf.thymeleafrs.entity.MstAccount;
import org.example.thymeleaf.thymeleafrs.exception.InvalidRegisterFieldException;
import org.example.thymeleaf.thymeleafrs.exception.PhoneNumberAlreadyExistsException;
import org.example.thymeleaf.thymeleafrs.exception.UsernameAlreadyExistsException;
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
        validateRegisterFields(registerRequest);
        validateDuplicate(registerRequest);
        MstAccount entity = toEntity(registerRequest);
        return mstAccountRepository.save(entity);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<MstAccount> userOpt = mstAccountRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPasswordHash(), userOpt.get().getPasswordHash())) {
            MstAccount user = userOpt.get();
            String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
            user.setToken(token);
            mstAccountRepository.save(user);
            return new LoginResponse(token);
        }
        throw new IllegalArgumentException("Login failed for user: " + loginRequest.getUsername());
    }

    private MstAccount toEntity(RegisterRequest request) {
        MstAccount account = new MstAccount();
        account.setUsername(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        account.setName(request.getName());
        account.setPhoneNumber(request.getPhoneNumber());
        account.setEmail(request.getEmail());
        account.setRole("USER");
        account.setToken("");
        return account;
    }

    private void validateRegisterFields(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new InvalidRegisterFieldException("Username harus diisi");
        }
        if (request.getPasswordHash() == null || request.getPasswordHash().isBlank()) {
            throw new InvalidRegisterFieldException("Password harus diisi");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRegisterFieldException("Nama harus diisi");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
            throw new InvalidRegisterFieldException("Nomor telepon harus diisi");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new InvalidRegisterFieldException("Email harus diisi");
        }
    }

    private void validateDuplicate(RegisterRequest request) {
        if (mstAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        if (mstAccountRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new PhoneNumberAlreadyExistsException(request.getPhoneNumber());
        }
    }
}
