// ErrorResponse.java - 统一错误响应类
package com.item.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "错误响应")
public class ErrorResponse {

    @Schema(description = "时间戳")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP状态码", example = "400")
    private int status;

    @Schema(description = "错误代码", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "错误消息", example = "参数验证失败")
    private String message;

    @Schema(description = "请求路径", example = "/api/users")
    private String path;

    @Schema(description = "错误详情")
    private Map<String, Object> details;

    /**
     * 创建验证错误响应
     */
    public static ErrorResponse validationError(String message, Map<String, Object> details, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message(message)
                .path(path)
                .details(details)
                .build();
    }

    /**
     * 创建业务错误响应
     */
    public static ErrorResponse businessError(String message, String errorCode, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errorCode)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * 创建认证错误响应
     */
    public static ErrorResponse authenticationError(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("AUTHENTICATION_ERROR")
                .message(message)
                .path(path)
                .build();
    }

    /**
     * 创建权限错误响应
     */
    public static ErrorResponse authorizationError(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("AUTHORIZATION_ERROR")
                .message(message)
                .path(path)
                .build();
    }

    /**
     * 创建资源未找到错误响应
     */
    public static ErrorResponse notFoundError(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("RESOURCE_NOT_FOUND")
                .message(message)
                .path(path)
                .build();
    }

    /**
     * 创建系统错误响应
     */
    public static ErrorResponse systemError(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message(message)
                .path(path)
                .build();
    }

    /**
     * 从异常创建错误响应
     */
    public static ErrorResponse fromException(Exception ex, String path) {
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            return authorizationError("权限不足，无法访问该资源", path);
        } else if (ex instanceof org.springframework.security.core.AuthenticationException) {
            return authenticationError("认证失败，请重新登录", path);
        } else if (ex instanceof com.item.management.exception.ResourceNotFoundException) {
            return notFoundError(ex.getMessage(), path);
        } else if (ex instanceof com.item.management.exception.BusinessException) {
            return businessError(ex.getMessage(), "BUSINESS_ERROR", path);
        } else if (ex instanceof javax.validation.ConstraintViolationException) {
            return validationError("参数验证失败", null, path);
        } else {
            return systemError("系统繁忙，请稍后重试", path);
        }
    }
}