// EnumValidator.java - 枚举验证器
package com.item.management.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private List<String> acceptedValues;
    private boolean nullable;

    @Override
    public void initialize(ValidEnum annotation) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        this.nullable = annotation.nullable();

        this.acceptedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return nullable;
        }

        return acceptedValues.contains(value.toUpperCase());
    }
}