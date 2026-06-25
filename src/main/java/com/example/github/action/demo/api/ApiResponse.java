package com.example.github.action.demo.api;

import java.time.Instant;

public class ApiResponse<T> {
    private String status;
    private int code;
    private String message;
    private T data;
    private Instant timestamp = Instant.now();

    public ApiResponse() {}

    public ApiResponse(String status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
