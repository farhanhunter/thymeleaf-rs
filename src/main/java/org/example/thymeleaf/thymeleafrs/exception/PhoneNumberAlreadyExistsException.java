package org.example.thymeleaf.thymeleafrs.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {
    public PhoneNumberAlreadyExistsException(String phoneNumber) {
        super("Nomor telepon " + phoneNumber + " sudah terdaftar.");
    }
}
