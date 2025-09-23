package org.example.thymeleaf.thymeleafrs.exception;

import org.example.thymeleaf.thymeleafrs.constant.SourceType;

public class PhoneInfoDuplicateException extends RuntimeException {
    public PhoneInfoDuplicateException(String phone, SourceType source) {
        super("Nomor " + phone + " dengan source " + source + " sudah terdaftar.");
    }
}
