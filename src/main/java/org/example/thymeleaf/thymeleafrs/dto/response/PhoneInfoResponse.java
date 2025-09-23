package org.example.thymeleaf.thymeleafrs.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.thymeleaf.thymeleafrs.constant.SourceType;

import java.util.List;

@Getter
@Setter
public class PhoneInfoResponse {
    private String phone;
    private String name;
    private SourceType source;
    private List<String> tags;
    private String createdBy;
    private String updatedBy;
}
