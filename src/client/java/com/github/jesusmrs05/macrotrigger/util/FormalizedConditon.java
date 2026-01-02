package com.github.jesusmrs05.macrotrigger.util;

import java.util.function.Predicate;

public class FormalizedConditon {
    private MacroTarget macroTarget;
    private String targetValue;
    private MacroCondition condition;
    private boolean negate;

    public FormalizedConditon(MacroTarget macroTarget, String targetValue, MacroCondition condition, boolean negate) {
        this.macroTarget = macroTarget;
        this.targetValue = targetValue;
        this.condition = condition;
        this.negate = negate;
    }

    public boolean test() {
        boolean result;
        result = this.condition.test(macroTarget.getTargetValue(), targetValue);
        result = negate ? !result : result;
        return result;
    }

    public MacroTarget getMacroTarget() { return macroTarget; }
    public MacroCondition getCondition() { return condition; }
    public String getTargetValue() { return targetValue; }

    public void setMacroTarget(MacroTarget target) {
        this.macroTarget = target;
        if (!target.admitsCondition(condition)) {
            this.condition = target.getConditions()[0];
        }
    }

    public void setCondition(MacroCondition condition) { this.condition = condition; }
    public void setTargetValue(String value) { this.targetValue = value; }
    public void setNegate(boolean negate) { this.negate = negate; }
    public boolean isNegate() { return this.negate; }
    public Predicate<String> getPredicate() {
        return dummy -> this.condition.test(this.macroTarget.getTargetValue(), this.targetValue);
    }
}
