package org.example.thymeleaf.thymeleafrs.exception;

public class PhoneInfoDuplicateException extends RuntimeException {
    public PhoneInfoDuplicateException(String phone, String source) {
        super("Nomor " + phone + " dengan source " + source + " sudah terdaftar.");
    }
}
