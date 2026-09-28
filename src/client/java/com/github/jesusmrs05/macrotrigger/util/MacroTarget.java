package com.github.jesusmrs05.macrotrigger.util;

import com.github.jesusmrs05.macrotrigger.client.ChatState;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public enum MacroTarget {
    HEALTH(
            MacroCondition.NUMERIC_CONDITIONS,
            new MacroTargetValueType[]{MacroTargetValueType.INTEGER},
            () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return 0;
                return (int) mc.player.getHealth();
            },
            "20"
    ),

    HUNGER(
            MacroCondition.NUMERIC_CONDITIONS,
            new MacroTargetValueType[]{MacroTargetValueType.INTEGER},
            () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return 0;
                return mc.player.getFoodData().getFoodLevel();
            },
            "20"
    ),

    CHAT(
            MacroCondition.TEXT_CONDITIONS,
            new MacroTargetValueType[]{MacroTargetValueType.ANYTHING},
            ChatState::getLastMessage,
            "Hello world!"
    ),

    TIMER(
            new MacroCondition[]{MacroCondition.TIMER_ENDED},
            new MacroTargetValueType[]{MacroTargetValueType.ANYTHING},
            () -> TimerManager.class,
            "someTimerThatLasts1second"
    );


    private final MacroCondition[] conditions;
    private final MacroTargetValueType[] valueTypes;
    private final Supplier<Object> targetValueGetter;
    private final String exampleValue;

    MacroTarget(
            MacroCondition[] conditions,
            MacroTargetValueType[] valueTypes,
            Supplier<Object> targetValueGetter,
            String exampleValue
    ) {
        this.conditions = conditions;
        this.valueTypes = valueTypes;
        this.targetValueGetter = targetValueGetter;
        this.exampleValue = exampleValue;
    }

    public Object getTargetValue() {
        return this.targetValueGetter.get();
    }

    public String getExampleValue() {
        return this.exampleValue;
    }

    public boolean admitsCondition(MacroCondition condition) {
        for (MacroCondition admittedCondition : this.conditions) {
            if (admittedCondition == condition) {
                return true;
            }
        }

        return false;
    }

    public boolean admitsValue(MacroTargetValueType valueType) {
        for (MacroTargetValueType admittedValueType : this.valueTypes) {
            if (admittedValueType == valueType) {
                return true;
            }
        }

        return false;
    }

    public MacroCondition[] getConditions() {
        return this.conditions;
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}