package org.example.thymeleaf.thymeleafrs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "mst_account")
public class MstAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20, nullable = false)
    private String role = "USER";

    @Column(nullable = false, length = 255)
    private String token;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(nullable = false)
    private boolean accountLocked = false;

    @Column
    private LocalDateTime lockTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public boolean isAccountLocked() {
        if (accountLocked && lockTime != null && lockTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            resetFailedLogin();
            return false;
        }
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    public void incrementFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 3) {
            this.accountLocked = true;
            this.lockTime = LocalDateTime.now();
        }
    }

    public void resetFailedLogin() {
        this.failedLoginAttempts = 0;
        this.accountLocked = false;
        this.lockTime = null;
    }
}
