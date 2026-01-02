package com.github.jesusmrs05.macrotrigger.util;

import com.ibm.icu.impl.number.range.PrefixInfixSuffixLengthHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class Macro {

    private String name;

    private List<FormalizedAction> actions = new ArrayList<>();

    private List<FormalizedConditon> conditions = new ArrayList<>();
    private List<LogicOperator> operators = new ArrayList<>();

    public Macro() {}

    public Macro(String name, List<FormalizedAction> actions, List<FormalizedConditon> conditions, List<LogicOperator> operators) {
        if (actions == null || conditions == null || operators == null) {
            throw new IllegalArgumentException("Macro lists cannot be null");
        }

        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
        this.operators.addAll(operators);
        this.name = name;

        normalize();
    }

    public void normalize() {
        if (actions == null) actions = new ArrayList<>();
        if (conditions == null) conditions = new ArrayList<>();
        if (operators == null) operators = new ArrayList<>();

        while (operators.size() < conditions.size()) {
            operators.add(null);
        }

        while (operators.size() > conditions.size()) {
            operators.remove(operators.size() - 1);
        }


        if (!operators.isEmpty()) {
            operators.set(0, null);
        }
    }

    public void addCondition(FormalizedConditon cond, LogicOperator operator) {
        Objects.requireNonNull(cond);
        conditions.add(cond);
        operators.add(operator);
        normalize();
    }

    public void addConditionAt(int index, FormalizedConditon cond, LogicOperator operator) {
        Objects.requireNonNull(cond);
        if (index < 0) index = 0;
        if (index > conditions.size()) index = conditions.size();
        conditions.add(index, cond);
        operators.add(index, operator);
        normalize();
    }

    public void removeCondition(int index) {
        if (index < 0 || index >= conditions.size()) return;
        conditions.remove(index);
        if (index < operators.size()) operators.remove(index);
        normalize();
    }

    public void addAction(FormalizedAction action) {
        Objects.requireNonNull(action);
        actions.add(action);
    }

    public void addActionAt(int index, FormalizedAction action) {
        Objects.requireNonNull(action);
        if (index < 0) index = 0;
        if (index > actions.size()) index = actions.size();
        actions.add(index, action);
    }

    public void removeAction(int index) {
        if (index < 0 || index >= actions.size()) return;
        actions.remove(index);
    }

    public boolean shouldExecute() {
        if (conditions.isEmpty()) return false;

        BooleanSupplier combined = LogicOperator.combine(conditions, operators);
        return combined.getAsBoolean();
    }

    public void execute() {
        for (FormalizedAction a : actions) {
            a.execute();
        }
    }

    public List<FormalizedConditon> getConditions() {
        return conditions;
    }

    public List<LogicOperator> getOperators() {
        return operators;
    }

    public List<FormalizedAction> getActions() {
        return actions;
    }

    public boolean containsAnyTarget(MacroTarget[] targets) {
        boolean result = false;
        for (FormalizedConditon condition : this.conditions) {
            for (MacroTarget target : targets) {
                if (condition.getMacroTarget().equals(target)) {
                    result = true;
                    break;
                }
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
