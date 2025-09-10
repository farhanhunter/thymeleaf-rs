package org.example.thymeleaf.thymeleafrs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@Data
@Table(name = "mst_account")
public class MstAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20, nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(nullable = false)
    private boolean accountLocked = false;

    @Column
    private LocalDateTime lockTime;

    @Column(length = 64)
    private String tokenHash;

    public boolean isAccountLocked() {
        if (accountLocked && lockTime != null && lockTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            resetFailedLogin();
            return false;
        }
        return accountLocked;
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
