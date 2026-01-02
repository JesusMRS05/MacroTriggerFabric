package com.github.jesusmrs05.macrotrigger.util;

public class FormalizedAction {
    private MacroAction action;
    private String actionData;

    public FormalizedAction(MacroAction action, String actionData) {
        this.action = action;
        this.actionData = actionData;
    }

    public void execute() {
        this.action.execute(this.actionData);
    }

    public MacroAction getAction() {
        return action;
    }
    public String getActionData() {
        return actionData;
    }
}
