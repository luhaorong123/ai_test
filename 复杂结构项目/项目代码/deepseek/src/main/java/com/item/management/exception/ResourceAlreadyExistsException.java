// ResourceAlreadyExistsException.java - 资源已存在异常
package com.item.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceAlreadyExistsException extends BusinessException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s 已存在，%s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT.value());
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value());
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
}