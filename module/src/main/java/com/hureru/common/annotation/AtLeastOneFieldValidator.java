package com.hureru.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author zheng
 */
public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        // 由@NotNull等其他注解处理空对象
        if (object == null) {
            return true;
        }

        boolean isValid = Arrays.stream(object.getClass().getDeclaredFields())
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(object);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .anyMatch(Objects::nonNull);

        // 如果验证失败，自定义错误消息
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("atLeastOneField")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
