package ru.yandex.practicum.filmorate.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SortDirectValidator implements ConstraintValidator<SortDirect, String> {
    public final void initialize(final SortDirect annotation) {}

    public final boolean isValid(final String sortParam, final ConstraintValidatorContext context) {
        List<String> expectParams = List.of("year", "likes");
        return expectParams.contains(sortParam);
    }
}
