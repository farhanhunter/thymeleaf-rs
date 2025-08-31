package org.example.thymeleaf.thymeleafrs.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String passwordHash;
    private String name;
    private String phoneNumber;
    private String email;
}
