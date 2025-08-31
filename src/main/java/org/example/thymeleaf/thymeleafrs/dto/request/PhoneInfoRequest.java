package org.example.thymeleaf.thymeleafrs.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PhoneInfoRequest {
    private String phone;
    private String name;
    private String source;
    private List<String> tags;
}
