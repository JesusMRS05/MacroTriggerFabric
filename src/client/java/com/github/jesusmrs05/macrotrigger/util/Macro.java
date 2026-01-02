package com.github.jesusmrs05.macrotrigger.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class Macro {

    // actions executed when the combined conditions evaluate to true
    private List<FormalizedAction> actions = new ArrayList<>();

    // conditions and operators that form the trigger expression
    private List<FormalizedConditon> conditions = new ArrayList<>();
    private List<LogicOperator> operators = new ArrayList<>();

    // default constructor for deserialization
    public Macro() {
        // keep lists initialized
    }

    // convenience constructor
    public Macro(List<FormalizedAction> actions, List<FormalizedConditon> conditions, List<LogicOperator> operators) {
        if (actions == null || conditions == null || operators == null) {
            throw new IllegalArgumentException("Macro lists cannot be null");
        }

        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
        this.operators.addAll(operators);

        normalize(); // enforce invariants
    }

    /**
     * Ensure internal invariants:
     * - lists are not null
     * - operators length >= conditions length (we keep same length)
     * - operators[0] == null
     */
    public void normalize() {
        if (actions == null) actions = new ArrayList<>();
        if (conditions == null) conditions = new ArrayList<>();
        if (operators == null) operators = new ArrayList<>();

        // make operators the same size as conditions
        while (operators.size() < conditions.size()) {
            operators.add(null);
        }
        // if operators is larger, trim extras (defensive)
        while (operators.size() > conditions.size()) {
            operators.remove(operators.size() - 1);
        }

        // first operator must be null by convention
        if (!operators.isEmpty()) {
            operators.set(0, null);
        }
    }

    // -----------------
    // Condition API
    // -----------------

    /** Adds a new condition at the end, operator applies before this condition (use null for first). */
    public void addCondition(FormalizedConditon cond, LogicOperator operator) {
        Objects.requireNonNull(cond);
        conditions.add(cond);
        // operator must be associated to this condition index
        operators.add(operator);
        // ensure invariants
        normalize();
    }

    /** Inserts a condition at index. operator is stored at the same index. */
    public void addConditionAt(int index, FormalizedConditon cond, LogicOperator operator) {
        Objects.requireNonNull(cond);
        if (index < 0) index = 0;
        if (index > conditions.size()) index = conditions.size();
        conditions.add(index, cond);
        operators.add(index, operator);
        normalize();
    }

    /** Removes condition (and its operator). Keeps operator[0] == null if possible. */
    public void removeCondition(int index) {
        if (index < 0 || index >= conditions.size()) return;
        conditions.remove(index);
        // remove correspondent operator (operators aligned to conditions)
        if (index < operators.size()) operators.remove(index);
        normalize();
    }

    // -----------------
    // Actions API
    // -----------------

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

    // -----------------
    // Execution API
    // -----------------

    public boolean shouldExecute() {
        // if there are no conditions, perhaps we treat as true? Decide policy:
        // here we treat "no conditions" as false (nothing to trigger).
        if (conditions.isEmpty()) return false;

        // build supplier from current conditions and operators
        BooleanSupplier combined = LogicOperator.combine(conditions, operators);
        return combined.getAsBoolean();
    }

    public void execute() {
        for (FormalizedAction a : actions) {
            a.execute();
        }
    }

    // -----------------
    // Accessors
    // -----------------

    public List<FormalizedConditon> getConditions() {
        return conditions;
    }

    public List<LogicOperator> getOperators() {
        return operators;
    }

    public List<FormalizedAction> getActions() {
        return actions;
    }
}
