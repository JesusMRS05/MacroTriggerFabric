package com.github.jesusmrs05.macrotrigger.util;

import java.util.ArrayList;
import java.util.List;

public class MacroFactory {

    public static Macro defaultMacro() {
        MacroTarget target = MacroTarget.HEALTH;

        List<FormalizedAction> actions = new ArrayList<>();
        actions.add(new FormalizedAction(MacroAction.SEND_TO_CHAT, ""));

        List<FormalizedConditon> conditions = new ArrayList<>();
        conditions.add(new FormalizedConditon(
                target,
                "20",
                target.getConditions()[0],
                false
        ));

        List<LogicOperator> operators = new ArrayList<>();
        operators.add(null); // 👈 permitido en ArrayList

        return new Macro(actions, conditions, operators);
    }

}