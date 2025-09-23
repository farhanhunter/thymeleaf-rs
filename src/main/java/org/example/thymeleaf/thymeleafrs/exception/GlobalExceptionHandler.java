package org.example.thymeleaf.thymeleafrs.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.thymeleaf.thymeleafrs.dto.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // === Helper builder ===
    private static <T> ResponseEntity<BaseResponse<T>> body(HttpStatusCode status, String code, String message, T data) {
        boolean is2xx = status.is2xxSuccessful();
        return ResponseEntity.status(status).body(new BaseResponse<>(code, !is2xx, message, data));
    }


    // ===== 4xx: domain & auth =====
    @ExceptionHandler(PhoneInfoNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> notFound(PhoneInfoNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, "404", ex.getMessage(), null);
    }

    @ExceptionHandler(PhoneInfoDuplicateException.class)
    public ResponseEntity<BaseResponse<Object>> duplicate(PhoneInfoDuplicateException ex) {
        return body(HttpStatus.CONFLICT, "409", ex.getMessage(), null);
    }

    @ExceptionHandler({ UsernameAlreadyExistsException.class, PhoneNumberAlreadyExistsException.class })
    public ResponseEntity<BaseResponse<Object>> conflicts(RuntimeException ex) {
        return body(HttpStatus.CONFLICT, "409", ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidRegisterFieldException.class)
    public ResponseEntity<BaseResponse<Object>> badRequest(InvalidRegisterFieldException ex) {
        return body(HttpStatus.BAD_REQUEST, "400", ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> badCred(BadCredentialsException ex) {
        return body(HttpStatus.UNAUTHORIZED, "401", ex.getMessage(), null);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<BaseResponse<Object>> locked(LockedException ex) {
        return body(HttpStatus.LOCKED, "423", ex.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> forbidden(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "403", "You don't have permission to access this resource", null);
    }

    // ===== 4xx: validasi & request shape =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleBeanValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }
        return body(HttpStatus.BAD_REQUEST, "400", "Validation failed", errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Object>> missingParam(MissingServletRequestParameterException ex) {
        return body(HttpStatus.BAD_REQUEST, "400", "Missing parameter: " + ex.getParameterName(), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> typeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = "Invalid value for '" + ex.getName() + "'";
        return body(HttpStatus.BAD_REQUEST, "400", msg, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> unreadable(HttpMessageNotReadableException ex) {
        return body(HttpStatus.BAD_REQUEST, "400", "Malformed JSON request", null);
    }

    // ===== WebClient (propagasi status dari remote) =====
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<BaseResponse<Object>> webClient(WebClientResponseException ex) {
        int code = ex.getStatusCode().value();
        log.warn("Downstream error {} {} body={}", code, ex.getStatusText(), ex.getResponseBodyAsString());
        String msg = ex.getStatusCode().is4xxClientError() ? "Upstream request failed" : "Upstream service error";
        return body(ex.getStatusCode(), String.valueOf(code), msg, null);
    }



    // ===== DB/constraint =====
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> dbConstraint(DataIntegrityViolationException ex) {
        log.warn("Constraint violation", ex);
        return body(HttpStatus.CONFLICT, "409", "Data conflict / constraint violation", null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<BaseResponse<Object>> db(DataAccessException ex) {
        log.error("Database error", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Database error", null);
    }

    // ===== Catch-all 500 (jangan bocorin pesan) =====
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Object>> runtime(RuntimeException ex) {
        String errorId = UUID.randomUUID().toString();
        log.error("Unhandled runtime error id={}", errorId, ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "500",
                "Terjadi kesalahan di server. Error ID: " + errorId, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> generic(Exception ex) {
        String errorId = UUID.randomUUID().toString();
        log.error("Unhandled error id={}", errorId, ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "500",
                "Terjadi kesalahan di server. Error ID: " + errorId, null);
    }
}
