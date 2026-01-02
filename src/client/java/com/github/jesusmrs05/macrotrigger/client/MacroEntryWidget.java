package com.github.jesusmrs05.macrotrigger.client;

import com.github.jesusmrs05.macrotrigger.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MacroEntryWidget {

    private final Macro macro;
    private final Runnable onRemove;
    private final int x;
    private final int y;
    private final int width;

    private final List<AbstractWidget> widgets = new ArrayList<>();

    public MacroEntryWidget(Macro macro, int x, int y, int width, Runnable onRemove) {
        this.macro = macro;
        this.x = x;
        this.y = y;
        this.width = width;
        this.onRemove = onRemove;

        build();
    }

    private void build() {
        widgets.clear();

        int cy = y;

        // TARGET
        widgets.add(
                CycleButton.builder(
                                v -> Component.literal(v.toString()),
                                macro.getCondition().getMacroTarget()
                        ).withValues(MacroTarget.values())
                        .create(x, cy, width, 20, Component.literal("Target"),
                                (btn, value) -> {
                                    macro.getCondition().setMacroTarget(value);
                                    build();
                                })
        );
        cy += 24;

        // CONDITION
        MacroCondition[] conditions =
                macro.getCondition().getMacroTarget().getConditions();

        widgets.add(
                CycleButton.builder(
                                v -> Component.literal(v.toString()),
                                macro.getCondition().getCondition()
                        ).withValues(conditions)
                        .create(x, cy, width, 20, Component.literal("Condition"),
                                (btn, value) -> macro.getCondition().setCondition(value))
        );
        cy += 24;

        // NEGATE
        widgets.add(
                CycleButton.booleanBuilder(
                        Component.literal("Yes"),
                        Component.literal("No"),
                        macro.getCondition().isNegate()
                ).create(x, cy, width, 20, Component.literal("Negate"),
                        (btn, value) -> macro.getCondition().setNegate(value))
        );
        cy += 24;

        // TARGET VALUE
        EditBox targetValue = new EditBox(
                Minecraft.getInstance().font,
                x, cy, width, 20,
                Component.literal("Target Value")
        );
        targetValue.setValue(macro.getCondition().getTargetValue());
        targetValue.setResponder(macro.getCondition()::setTargetValue);
        widgets.add(targetValue);
        cy += 24;

        // ACTION
        widgets.add(
                CycleButton.builder(
                                v -> Component.literal(v.toString()),
                                macro.getAction()
                        ).withValues(MacroAction.values())
                        .create(x, cy, width, 20, Component.literal("Action"),
                                (btn, value) -> macro.setAction(value))
        );
        cy += 24;

        // ACTION DATA
        EditBox actionData = new EditBox(
                Minecraft.getInstance().font,
                x, cy, width, 20,
                Component.literal("Action Data")
        );
        actionData.setValue(macro.getActionData());
        actionData.setResponder(macro::setActionData);
        widgets.add(actionData);
        cy += 24;

        // REMOVE
        widgets.add(
                Button.builder(Component.literal("Remove"), btn -> onRemove.run())
                        .bounds(x, cy, width, 20)
                        .build()
        );
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    public int getHeight() {
        return widgets.size() * 24;
    }
}
