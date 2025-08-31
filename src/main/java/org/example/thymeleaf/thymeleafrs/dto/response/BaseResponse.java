package org.example.thymeleaf.thymeleafrs.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@JsonPropertyOrder({ "reqId", "status", "error", "message", "data" })
@Getter
@Setter
public class BaseResponse<T> {
    private String reqId;
    private String status;
    private boolean error;
    private String message;
    private T data;

    public BaseResponse(String status, boolean error, String message, T data) {
        this.reqId = UUID.randomUUID().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.data = data;
    }
}
