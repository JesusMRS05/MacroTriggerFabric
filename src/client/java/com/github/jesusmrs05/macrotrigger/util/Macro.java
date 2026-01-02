package com.github.jesusmrs05.macrotrigger.util;

public class Macro {
    private MacroAction action;
    private FormalizedConditon condition;
    private String actionData;

    public Macro(MacroAction action, FormalizedConditon condition, String actionData) {
        this.action = action;
        this.condition = condition;
        this.actionData = actionData;
    }

    public boolean shouldExecute() {
        return this.condition.test();
    }

    public void execute() {
        this.action.execute(this.actionData);
    }

    public FormalizedConditon getCondition() { return condition; }
    public MacroAction getAction() { return action; }
    public String getActionData() { return actionData; }
    public void setAction(MacroAction action) { this.action = action; }
    public void setActionData(String data) { this.actionData = data; }

}
