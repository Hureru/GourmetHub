package com.hureru.iam.exception;

import lombok.Getter;

import java.util.Map;

/**
 * @author zheng
 */
@Getter
public class BusinessException extends RuntimeException {
    // 响应代码
    private final Integer code;
    // 错误类型（如："VALIDATION_ERROR"）
    private final String errorType;
    // 错误详情
    private final Map<String, Object> details;

    public BusinessException(Integer code, String message) {
        this(code, "BUSINESS_ERROR", message, null);
    }

    public BusinessException(Integer code, String errorType, String message) {
        this(code, errorType, message, null);
    }

    public BusinessException(Integer code, String errorType, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.errorType = errorType;
        this.details = details;
    }

}
