package com.library.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    // 客户端错误（400系）
    public static <T> ApiResponse<T> clientError(String message) {
        return new ApiResponse<>(400, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> clientError(Integer code, String message) {
        return new ApiResponse<>(code, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> clientError(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data, LocalDateTime.now());
    }

    // 服务器错误（500系）
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(500, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> serverError(Integer code, String message) {
        return new ApiResponse<>(code, message, null, LocalDateTime.now());
    }
}