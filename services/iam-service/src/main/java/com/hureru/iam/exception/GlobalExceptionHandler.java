package com.hureru.iam.exception;

import com.hureru.common.R;
import com.hureru.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zheng
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<R> handleBindingValidation(Exception ex) {
        BindingResult bindingResult = ex instanceof MethodArgumentNotValidException
                ? ((MethodArgumentNotValidException) ex).getBindingResult()
                : ((BindException) ex).getBindingResult();

        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error("[Binding]参数验证失败：{}", errors);
        return ResponseEntity.badRequest()
                .body(R.error(400, "参数校验失败：" + errors));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<R> handleMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getAllValidationResults().forEach(result -> result.getResolvableErrors().forEach(error -> errors.put(result.getMethodParameter().getParameterName(), error.getDefaultMessage())));
        log.error("[Method]参数验证失败: {}", errors);
        return ResponseEntity.badRequest()
                .body(R.error(400, "参数校验失败：" + errors));
    }

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseEntity.badRequest().body(R.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<R> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据已存在: {}", e.getMessage());
        return ResponseEntity.badRequest().body(R.error(409, "数据已存在"));
    }

}
