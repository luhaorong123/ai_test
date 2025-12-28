// ResourceNotFoundException.java - 资源未找到异常
package com.item.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends BusinessException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s 未找到，%s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND.value());
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
}