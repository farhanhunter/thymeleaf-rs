package org.example.thymeleaf.thymeleafrs.dto.response;

import java.util.UUID;

public class BaseResponse<T> {
    private String reqId;
    private String status;
    private boolean error;
    private String message;
    private T data;

    public BaseResponse() {
        this.reqId = UUID.randomUUID().toString();
    }

    public BaseResponse(String status, boolean error, String message, T data) {
        this.reqId = UUID.randomUUID().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
