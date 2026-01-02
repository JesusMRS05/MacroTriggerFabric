package com.github.jesusmrs05.macrotrigger.util;

import java.util.function.BiPredicate;

public enum MacroCondition {
    LESSER_THAN((value, targetValue) ->
            parse(value) < parse(targetValue)
    ),

    LESSER_OR_EQUAL((value, targetValue) ->
            parse(value) <= parse(targetValue)
    ),

    GREATER_THAN((value, targetValue) ->
            parse(value) > parse(targetValue)
    ),

    GREATER_OR_EQUAL((value, targetValue) ->
            parse(value) >= parse(targetValue)
    ),

    EQUAL((value, targetValue) ->
            parse(value) == parse(targetValue)
    ),

    INCLUDES_TEXT((value, targetValue) ->
            value.toString().contains(targetValue)
    ),

    IS_TEXT((value, targetValue) ->
            value.toString().equals(targetValue)
    ),

    INCLUDES_TEXT_IGNORE_CASE((value, targetValue) ->
            value.toString().toLowerCase()
                    .contains(targetValue.toLowerCase())
    ),

    IS_TEXT_IGNORE_CASE((value, targetValue) ->
            value.toString().equalsIgnoreCase(targetValue)
    ),

    MATCHES_REGEX((value, targetValue) ->
            value.toString().matches(targetValue)
    );

    public static final MacroCondition[] NUMERIC_CONDITIONS = new MacroCondition[] {
            MacroCondition.LESSER_THAN,
            MacroCondition.LESSER_OR_EQUAL,
            MacroCondition.GREATER_THAN,
            MacroCondition.GREATER_OR_EQUAL,
            MacroCondition.EQUAL
    };

    public static final MacroCondition[] TEXT_CONDITIONS = new MacroCondition[] {
            MacroCondition.INCLUDES_TEXT,
            MacroCondition.IS_TEXT,
            MacroCondition.INCLUDES_TEXT_IGNORE_CASE,
            MacroCondition.IS_TEXT_IGNORE_CASE,
            MacroCondition.MATCHES_REGEX
    };

    private final BiPredicate<Object, String> condition;

    MacroCondition(BiPredicate<Object, String> condition) {
        this.condition = condition;
    }

    public boolean test(Object value, String targetValue) {
        return condition.test(value, targetValue);
    }

    private static double parse(Object o) {
        return Double.parseDouble(o.toString());
    }

    @Override
    public String toString() {
        String[] parts = this.name().split("_");
        String result = parts[0].charAt(0) + parts[0].substring(1).toLowerCase();
        for (int i = 1 ; i < parts.length; i++) {
            result = result + " " + parts[i].charAt(0) + parts[i].substring(1).toLowerCase();
        }
        return result;
    }
}
