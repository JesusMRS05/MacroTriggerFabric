package com.github.jesusmrs05.macrotrigger.util;

import com.github.jesusmrs05.macrotrigger.client.ChatState;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Supplier;

public enum MacroTarget {
    HEALTH(
            MacroCondition.NUMERIC_CONDITIONS,
            new MacroTargetValueType[]{MacroTargetValueType.INTEGER},
            () -> {
                Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.player == null) return 0;
                return (int) mc.player.getHealth();
            }
    ),

    HUNGER(
            MacroCondition.NUMERIC_CONDITIONS,
            new MacroTargetValueType[]{MacroTargetValueType.INTEGER},
            () -> {
                Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.player == null) return 0;
                return mc.player.getFoodData().getFoodLevel();
            }
    ),

    CHAT(
            MacroCondition.TEXT_CONDITIONS,
            new MacroTargetValueType[]{MacroTargetValueType.ANYTHING},
            ChatState::getLastMessage
    );

    private MacroCondition[] conditions;
    private MacroTargetValueType[] admittedValueTypes;
    private Supplier<Object> targetValueGetter;

    MacroTarget(MacroCondition[] conditions,  MacroTargetValueType[] admittedValueTypes,  Supplier<Object> targetValueGetter) {
        this.conditions = conditions;
        this.admittedValueTypes = admittedValueTypes;
        this.targetValueGetter = targetValueGetter;
    }

    public boolean admitsCondition(MacroCondition condition) {
        return List.of(this.conditions).contains(condition);
    }

    public Object getTargetValue() {
        return this.targetValueGetter.get();
    }

    public boolean admitsValue(Object value) {
        boolean admitted = false;
        for (MacroTargetValueType admittedValueType : this.admittedValueTypes) {
            if (admittedValueType.isValid(value)) {
                admitted = true;
                break;
            }
        }
        return admitted;
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }

    public MacroCondition[] getConditions() {
        return this.conditions;
    }

    private static String lastChatMessage = "";

    public static void setLastChatMessage(String msg) {
        lastChatMessage = msg;
    }
}