package com.github.jesusmrs05.macrotrigger.util;

import java.util.function.Predicate;

public enum MacroTargetValueType {
    INTEGER(value -> value instanceof Integer),
    ANYTHING(value -> true);

    private final Predicate<Object> validator;

    MacroTargetValueType(Predicate<Object> validator) {
        this.validator = validator;
    }

    public boolean isValid(Object value) {
        return this.validator.test(value);
    }
}
