package com.taskmanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SafeHtml.SanitizerValidator.class)
public @interface SafeHtml {

    String message() default "Contains potentially unsafe content";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SanitizerValidator implements ConstraintValidator<SafeHtml, String> {

        private static final String[] BLOCKED_PATTERNS = {
                "<script", "javascript:", "onerror=", "onload=",
                "onclick=", "onmouseover=", "onfocus=", "onblur="
        };

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isEmpty()) {
                return true;
            }
            String lowerValue = value.toLowerCase();
            for (String pattern : BLOCKED_PATTERNS) {
                if (lowerValue.contains(pattern)) {
                    return false;
                }
            }
            return true;
        }
    }
}
