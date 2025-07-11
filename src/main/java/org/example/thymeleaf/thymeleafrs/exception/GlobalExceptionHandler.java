package org.example.thymeleaf.thymeleafrs.exception;

import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PhoneInfoNotFoundException.class)
    public BaseResponse<Object> handlePhoneInfoNotFound(PhoneInfoNotFoundException ex) {
        return new BaseResponse<>("404", true, ex.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<Object> handleRuntimeException(RuntimeException ex) {
        return new BaseResponse<>("500", true, "Terjadi kesalahan internal: " + ex.getMessage(), null);
    }
}
