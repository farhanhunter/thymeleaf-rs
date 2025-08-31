package org.example.thymeleaf.thymeleafrs.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String passwordHash;
}
