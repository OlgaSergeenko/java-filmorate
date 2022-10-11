package ru.yandex.practicum.filmorate.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SortSearchValidator implements ConstraintValidator<SortSearch, List<String>> {
    public final void initialize(final SortSearch annotation) {
    }

    public final boolean isValid(final List<String> sortParams, final ConstraintValidatorContext context) {
        List<String> expectParams = List.of("director", "title");

        return expectParams.containsAll(sortParams);
    }
}
