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

    @ExceptionHandler(PhoneInfoDuplicateException.class)
    public BaseResponse<Object> handlePhoneInfoDuplicate(PhoneInfoDuplicateException ex) {
        return new BaseResponse<>("409", true, ex.getMessage(), null);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public BaseResponse<Object> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return new BaseResponse<>("409", true, ex.getMessage(), null);
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public BaseResponse<Object> handlePhoneNumberExists(PhoneNumberAlreadyExistsException ex) {
        return new BaseResponse<>("409", true, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidRegisterFieldException.class)
    public BaseResponse<Object> handleInvalidField(InvalidRegisterFieldException ex) {
        return new BaseResponse<>("400", true, ex.getMessage(), null);
    }
}
