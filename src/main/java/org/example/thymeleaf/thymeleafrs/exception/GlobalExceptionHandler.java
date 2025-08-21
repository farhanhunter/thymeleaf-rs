package org.example.thymeleaf.thymeleafrs.exception;

import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PhoneInfoNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handlePhoneInfoNotFound(PhoneInfoNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new BaseResponse<>("404", true, ex.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .internalServerError()
                .body(new BaseResponse<>("500", true, "Terjadi kesalahan internal: " + ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .internalServerError()
                .body(new BaseResponse<>("500", true, "Unexpected error occurred" + ex.getMessage(), null));
    }

    @ExceptionHandler(PhoneInfoDuplicateException.class)
    public ResponseEntity<BaseResponse<Object>> handlePhoneInfoDuplicate(PhoneInfoDuplicateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new BaseResponse<>("409", true, ex.getMessage(), null));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Object>> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new BaseResponse<>("409", true, ex.getMessage(), null));
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Object>> handlePhoneNumberExists(PhoneNumberAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body( new BaseResponse<>("409", true, ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidRegisterFieldException.class)
    public ResponseEntity<BaseResponse<Object>> handleInvalidField(InvalidRegisterFieldException ex) {
        return ResponseEntity
                .badRequest()
                .body(new BaseResponse<>("400", true, ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new BaseResponse<>("401", true, ex.getMessage(), null));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<BaseResponse<Object>> handleLocked(LockedException ex) {
        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(new BaseResponse<>("423", true, ex.getMessage(), null));
    }
}
