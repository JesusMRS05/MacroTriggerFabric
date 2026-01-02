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

        if (predicates.size() != operators.size() + 1) {
            throw new IllegalArgumentException(
                    "predicates must have size operators + 1"
            );
        }

        BooleanSupplier result = () -> predicates.getFirst()
                .getPredicate()
                .test(null);

        for (int i = 0; i < operators.size(); i++) {
            int finalI = i;
            BooleanSupplier next = () -> predicates.get(finalI + 1)
                    .getPredicate()
                    .test(null);

            LogicOperator op = operators.get(i);

            BooleanSupplier left = result; // effectively final

            result = () -> op.operator.test(
                    left.getAsBoolean(),
                    next.getAsBoolean()
            );
        }

        return result;
    }
}