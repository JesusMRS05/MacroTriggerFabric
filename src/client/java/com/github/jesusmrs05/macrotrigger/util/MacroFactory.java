package com.github.jesusmrs05.macrotrigger.util;

public class MacroFactory {

    public static Macro defaultMacro() {
        MacroTarget target = MacroTarget.HEALTH;

        return new Macro(
                MacroAction.SEND_TO_CHAT,
                new FormalizedConditon(
                        target,
                        "20",
                        target.getConditions()[0],
                        false
                ),
                ""
        );
    }
}