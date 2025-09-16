package org.example.thymeleaf.thymeleafrs.fb.dto;

import lombok.Data;

@Data
public class FbMeDto {
    private String id;
    private String name;
    private String email;
    private Picture picture;

    @Data
    public static class Picture { public DataInner data; }
    @Data
    public static class DataInner { public String url; }
}
