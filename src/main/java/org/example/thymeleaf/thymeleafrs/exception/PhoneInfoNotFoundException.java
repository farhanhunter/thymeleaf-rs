package org.example.thymeleaf.thymeleafrs.exception;

public class PhoneInfoNotFoundException extends RuntimeException{
    public PhoneInfoNotFoundException(String phone) {
        super("Nomor " + phone + " tidak ditemukan di database.");
    }
}
