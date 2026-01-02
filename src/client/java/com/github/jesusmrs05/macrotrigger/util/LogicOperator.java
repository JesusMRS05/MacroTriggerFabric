package com.github.jesusmrs05.macrotrigger.util;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public enum LogicOperator {

    AND((a, b) -> a && b),
    OR((a, b) -> a || b),
    XOR((a, b) -> a ^ b),
    NAND((a, b) -> !(a && b)),
    NOR((a, b) -> !(a || b)),
    XNOR((a, b) -> a == b);

    private final BiPredicate<Boolean, Boolean> operator;

    LogicOperator(BiPredicate<Boolean, Boolean> operator) {
        this.operator = operator;
    }

    public static BooleanSupplier combine(
            List<FormalizedConditon> predicates,
            List<LogicOperator> operators
    ) {
        if (predicates.isEmpty()) {
            return () -> false;
        }

        if (operators.size() != predicates.size()) {
            throw new IllegalArgumentException(
                    "operators must have the same size as predicates (first operator must be null)"
            );
        }

        // Start with first predicate
        BooleanSupplier result = () ->
                predicates.get(0).getPredicate().test(null);

        // Combine from index 1 onwards
        for (int i = 1; i < predicates.size(); i++) {

            LogicOperator op = operators.get(i);
            if (op == null) {
                throw new IllegalStateException(
                        "Operator at index " + i + " is null (only index 0 may be null)"
                );
            }

            BooleanSupplier left = result;
            int finalI = i;
            BooleanSupplier right = () ->
                    predicates.get(finalI).getPredicate().test(null);

            result = () -> op.operator.test(
                    left.getAsBoolean(),
                    right.getAsBoolean()
            );
        }

        return result;
    }
}