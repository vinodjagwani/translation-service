/**
 * @author Vinod Jagwani
 */
package se.digitaltolk.translation.validator;

import static org.apache.commons.lang3.StringUtils.isBlank;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.springframework.lang.NonNull;
import se.digitaltolk.translation.validator.annotation.Enum;

public class EnumValueValidator implements ConstraintValidator<Enum, String> {

    private Enum annotation;

    private String[] enumValues;

    @Override
    public void initialize(@NonNull Enum annotation) {
        this.annotation = annotation;
        final Class<? extends java.lang.Enum<?>> enumClass = annotation.enumClass();
        this.enumValues = Arrays.stream(enumClass.getEnumConstants())
                .map(java.lang.Enum::name)
                .toArray(String[]::new);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) {
            return true;
        }
        return Arrays.stream(enumValues).anyMatch(enumValue -> matches(value, enumValue));
    }

    private boolean matches(String input, String enumValue) {
        return annotation.ignoreCase()
                ? input.equalsIgnoreCase(enumValue)
                : input.equals(enumValue);
    }
}