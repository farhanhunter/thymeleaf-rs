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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password.");
        }
        MstAccount user = userOpt.get();

        if (user.isAccountLocked()) {
            throw new LockedException("Account is locked due to multiple failed login attempts.");
        }

        if (!passwordEncoder.matches(loginRequest.getPasswordHash(), user.getPasswordHash())) {
            user.incrementFailedLogin();
            mstAccountRepository.save(user);
            throw new BadCredentialsException("Invalid username or password.");
        }

        user.resetFailedLogin();
        mstAccountRepository.save(user);

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole());

        user.setToken(token);
        mstAccountRepository.save(user);

        return new LoginResponse(token);
    }

    @Override
    public void unlockAccount(String username) {
        Optional<MstAccount> userOpt = mstAccountRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            MstAccount user = userOpt.get();
            user.resetFailedLogin();
            mstAccountRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
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
        account.setFailedLoginAttempts(0);
        account.setAccountLocked(false);
        account.setLockTime(null);
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
        if (request.getEmail() == null || request.getEmail().isBlank() || !isValidEmail(request.getEmail())) {
            throw new InvalidRegisterFieldException("Email harus diisi" + " dan harus dalam format yang benar");
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
