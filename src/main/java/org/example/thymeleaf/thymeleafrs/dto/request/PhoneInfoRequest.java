package org.example.thymeleaf.thymeleafrs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.thymeleaf.thymeleafrs.constant.SourceType;

import java.util.List;

@Getter
@Setter
public class PhoneInfoRequest {
    @NotBlank
    private String phone;

    @NotBlank
    private SourceType source;

    @NotBlank
    private String name;

    private List<String> tags;
}
